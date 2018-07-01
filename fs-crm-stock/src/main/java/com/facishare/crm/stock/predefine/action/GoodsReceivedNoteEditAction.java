package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 16/01/2018
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteEditAction extends CommonEditAction {

    private GoodsReceivedNoteManager goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

    private String oldLifeStatus;

    private IObjectData oldObjectData;
    private IObjectData newObjectData;
    private List<IObjectData> oldProductObjectDataList = Lists.newArrayList();
    private List<IObjectData> newProductObjectDataList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        log.debug("GoodsReceivedNoteEditAction.before. Arg[{}]", arg);
        goodsReceivedNoteManager.modifyArg(this.getActionContext().getTenantId(), arg);
        super.before(arg);
        oldObjectData = queryObjectData(arg);
        newObjectData = arg.getObjectData().toObjectData();
        oldProductObjectDataList = queryGoodsReceivedNoteProduct();
        oldLifeStatus = oldObjectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

        //"调拨入库"类型的入库单不允许编辑
        String recordType = oldObjectData.getRecordType();
        if (recordType.equals(GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "调拨入库类型的入库单不允许编辑");
        }

        //如果是生命状态为正常，不允许编辑只读字段
        if (!Objects.equals(oldLifeStatus,SystemConstants.LifeStatus.Ineffective.value)) {
            //校验入库单主只读字段
            checkReadOnlyField();
        }

        log.debug("mergeObjectData before[{}]", arg);
        // 由于OpenAPI的更新操作提交的数据是增量数据，需要与当前的系统数据合并
        mergeObjectData(arg);
        log.debug("mergeObjectData after[{}]", arg);

    }

    private List<IObjectData> mergeObjectData(Arg arg) {
        newObjectData = this.merge(oldObjectData, arg.getObjectData().toObjectData());
        arg.setObjectData(ObjectDataDocument.of(newObjectData));

        Map<String, IObjectData> id2CurrentProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        List<IObjectData> argProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(GoodsReceivedNoteProductConstants.API_NAME));
        for (IObjectData argProduct : argProductObjectDocList) {
            String goodsReceivedNoteProductId = Objects.nonNull(argProduct.getId()) ? argProduct.getId() : null;
            boolean isNewAdd = StringUtils.isBlank(goodsReceivedNoteProductId);
            if (isNewAdd) {
                newProductObjectDataList.add(argProduct);
            } else {
                if (!id2CurrentProductObjectDataMap.containsKey(goodsReceivedNoteProductId)) {
                    throw new ValidateException("不存在对应入库单产品记录");
                }
                newProductObjectDataList.add(this.merge(id2CurrentProductObjectDataMap.get(goodsReceivedNoteProductId), argProduct));
            }
        }
        arg.getDetails().remove(GoodsReceivedNoteProductConstants.API_NAME);
        arg.getDetails().put(GoodsReceivedNoteProductConstants.API_NAME, ObjectDataDocument.ofList(newProductObjectDataList));
        return newProductObjectDataList;
    }

    private List<IObjectData> queryGoodsReceivedNoteProduct() {
        IObjectDescribe goodsReceivedNoteProductDescribe = serviceFacade.findObject(actionContext.getTenantId(), GoodsReceivedNoteProductConstants.API_NAME);
        return serviceFacade.findDetailObjectDataList(goodsReceivedNoteProductDescribe, oldObjectData, actionContext.getUser());

    }

    private IObjectData queryObjectData(Arg arg) {
        String goodsReceivedNoteId = arg.getObjectData().toObjectData().getId();
        return goodsReceivedNoteManager.getObjectDataById(this.getActionContext().getUser(), goodsReceivedNoteId);
    }

    private IObjectData merge(IObjectData currentObjectData, IObjectData newObjectDate) {
        Map<String, Object> currentObjectDataMap = ObjectDataExt.of(currentObjectData).toMap();
        Map<String, Object> newObjectDataMap = ObjectDataExt.of(newObjectDate).toMap();
        newObjectDataMap.keySet().forEach(currentObjectDataMap::remove);
        currentObjectDataMap.putAll(newObjectDataMap);
        return ObjectDataExt.of(currentObjectDataMap).getObjectData();
    }

    private void checkReadOnlyField() {
        //主对象需要校验只读的字段
        List<String> masterReadOnlyFieldNames = Lists.newArrayList(GoodsReceivedNoteConstants.Field.Name.apiName,
                GoodsReceivedNoteConstants.Field.Warehouse.apiName);

        //从对象需要校验只读的字段
        List<String> detailsReadOnlyFieldNames = Lists.newArrayList(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.apiName,
                GoodsReceivedNoteProductConstants.Field.Product.apiName,
                GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName);

        //校验主对象只读字段
        checkReadOnlyField(oldObjectData, newObjectData, masterReadOnlyFieldNames);

        //校验入库单产品只读字段
        Map<String, IObjectData> id2OldProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        List<IObjectData> newProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(GoodsReceivedNoteProductConstants.API_NAME));
        if (CollectionUtils.isEmpty(newProductObjectDocList)) {
            throw new ValidateException("入库单产品产品为空");
        }
        newProductObjectDocList.forEach(newProductObjectData -> {
            if (Objects.isNull(newProductObjectData.getId())) {
                throw new ValidateException("正常状态下不可添加新的入库单产品");
            }
            String id = newProductObjectData.getId();
            if (!id2OldProductObjectDataMap.containsKey(id)) {
                throw new ValidateException("["+id+"]不属于当前入库单");
            }
            checkReadOnlyField(id2OldProductObjectDataMap.get(id), newProductObjectData, detailsReadOnlyFieldNames);
        });

        if (oldProductObjectDataList.size() != newProductObjectDocList.size()) {
            throw new ValidateException("不可变更入库单产品");
        }
    }

    private void checkReadOnlyField(IObjectData oldObjectData, IObjectData newObjectData, List<String> readOnlyFieldApiNames) {
        readOnlyFieldApiNames.forEach(readOnlyFieldApiName -> {
            if (Objects.nonNull(newObjectData.get(readOnlyFieldApiName))) {
                if ((oldObjectData.get(readOnlyFieldApiName).toString()).compareTo(newObjectData.get(readOnlyFieldApiName).toString()) != 0) {
                    throw new ValidateException(readOnlyFieldApiName + "不可修改");
                }
            }
        });
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        this.goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

        IObjectData objectDataResult = result.getObjectData().toObjectData();
        String newLifeStatus = objectDataResult.get(SystemConstants.Field.LifeStatus.apiName).toString();
        log.debug("GoodsReceivedNoteEditAction.after! newLifeStatus[{}], objectData[{}]", newLifeStatus, objectDataResult);
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
            if (newLifeStatus.equals(SystemConstants.LifeStatus.Normal.value)) {

                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.EDIT.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                        .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                        .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                GoodsReceivedNoteProductModel.BuildProductResult productVo = goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);
                //增加实际库存
                goodsReceivedNoteManager.insertOrUpdateStock(actionContext.getUser(), result.getObjectData().toObjectData(), productVo, stockOperateInfo);
            }
        }
        return result;
    }
}

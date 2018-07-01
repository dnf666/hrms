package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.requisitionnote.predefine.service.dto.RequisitionNoteType;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteEditAction extends CommonEditAction{
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;
    private RequisitionNoteManager requisitionNoteManager = SpringUtil.getContext().getBean(RequisitionNoteManager.class);

    String oldLifeStatus;
    IObjectData oldObjectData;
    IObjectData newObjectData;
    List<IObjectData> oldProductObjectDataList = Lists.newArrayList();
    List<IObjectData> newProductObjectDataList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        log.debug("GoodsReceivedNoteEditAction.before. Arg[{}]", arg);
        requisitionNoteManager.modifyArg(this.getActionContext().getTenantId(), arg);
        super.before(arg);
        oldObjectData = queryObjectData(arg);
        newObjectData = arg.getObjectData().toObjectData();
        oldProductObjectDataList = requisitionNoteManager.findDetailObjectDataList(this.actionContext.getUser(),
                RequisitionNoteProductConstants.API_NAME, oldObjectData);
        oldLifeStatus = oldObjectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

        //如果是生命状态为正常，不允许编辑只读字段
        if (!Objects.equals(oldLifeStatus,SystemConstants.LifeStatus.Ineffective.value)) {
            //校验只读字段
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

        Map<String, IObjectData> id2CurrentProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), Function.identity()));
        List<IObjectData> argProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(RequisitionNoteProductConstants.API_NAME));
        for (IObjectData argProduct : argProductObjectDocList) {
            String requisitionNoteProductId = Objects.nonNull(argProduct.getId()) ? argProduct.getId() : null;
            boolean isNewAdd = StringUtils.isBlank(requisitionNoteProductId);
            if (isNewAdd) {
                newProductObjectDataList.add(argProduct);
            } else {
                if (!id2CurrentProductObjectDataMap.containsKey(requisitionNoteProductId)) {
                    throw new ValidateException("不存在对应调拨单产品记录");
                }
                newProductObjectDataList.add(this.merge(id2CurrentProductObjectDataMap.get(requisitionNoteProductId), argProduct));
            }
        }
        arg.getDetails().remove(RequisitionNoteProductConstants.API_NAME);
        arg.getDetails().put(RequisitionNoteProductConstants.API_NAME, ObjectDataDocument.ofList(newProductObjectDataList));
        return newProductObjectDataList;
    }


    private IObjectData queryObjectData(Arg arg) {
        String dataId = arg.getObjectData().toObjectData().getId();
        return requisitionNoteManager.findById(this.getActionContext().getUser(), dataId, RequisitionNoteConstants.API_NAME);
    }

    private IObjectData merge(IObjectData currentObjectData, IObjectData newObjectDate) {
        Map<String, Object> currentObjectDataMap = ObjectDataExt.of(currentObjectData).toMap();
        Map<String, Object> newObjectDataMap = ObjectDataExt.of(newObjectDate).toMap();
        newObjectDataMap.keySet().forEach(currentObjectDataMap::remove);
        currentObjectDataMap.putAll(newObjectDataMap);
        return ObjectDataExt.of(currentObjectDataMap).getObjectData();
    }

    private void checkReadOnlyField() {
        //校验主对象只读字段
        checkReadOnlyField(oldObjectData, newObjectData, RequisitionNoteType.masterReadOnlyFields);

        //校验入库单产品只读字段
        Map<String, IObjectData> id2OldProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), Function.identity()));
        List<IObjectData> newProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(RequisitionNoteProductConstants.API_NAME));
        if (CollectionUtils.isEmpty(newProductObjectDocList)) {
            throw new ValidateException("调拨单产品为空");
        }
        newProductObjectDocList.forEach(newProductObjectData -> {
            if (Objects.isNull(newProductObjectData.getId())) {
                throw new ValidateException("正常状态下编辑不可添加新的调拨单产品");
            }
            String id = newProductObjectData.getId();
            if (!id2OldProductObjectDataMap.containsKey(id)) {
                throw new ValidateException("["+id+"]不属于当前调拨单");
            }
            checkReadOnlyField(id2OldProductObjectDataMap.get(id), newProductObjectData, RequisitionNoteType.detailsReadOnlyFields);
        });

        if (oldProductObjectDataList.size() != newProductObjectDocList.size()) {
            throw new ValidateException("不可变更调拨单产品");
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
        User user = actionContext.getUser();

        String lifeStatus = result.getObjectData().toObjectData().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("RequisitionNoteEditAction.after! LifeStatus[{}], objectData[{}], result[{}]", lifeStatus, objectData, result);

        String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

        requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);

        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
            //库存操作记录
            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.EDIT.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                    .afterLifeStatus(lifeStatus)
                    .operateObjectType(StockOperateObjectTypeEnum.REQUISITION_NOTE.value)
                    .build();

            //无流程 正常状态
            if (lifeStatus.equals(ObjectLifeStatus.NORMAL.getCode())) {
                List<ObjectDataDocument> productDocList = arg.getDetails().get(RequisitionNoteProductConstants.API_NAME);
                //1、创建一条出库单记录
                requisitionNoteManager.createOutboundDeliveryNote(user, objectData, transferOutWarehouseId, productDocList);


                //2、扣减调出仓库的实际库存
                requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);
                stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
                requisitionNoteCalculateManager.minusRealStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
            }

            //有流程 审批中状态
            if (lifeStatus.equals(ObjectLifeStatus.UNDER_REVIEW.getCode())) {
                //、增加调出仓库的冻结库存
                stockOperateInfo.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
                log.debug("addBlockedStock, user[{}], transferOutWarehouseId[{}], objectData[{}], stockOperateInfo[{}]",user, transferOutWarehouseId, objectData, stockOperateInfo);
                requisitionNoteCalculateManager.addBlockedStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
            }
        }
        return result;
    }
}

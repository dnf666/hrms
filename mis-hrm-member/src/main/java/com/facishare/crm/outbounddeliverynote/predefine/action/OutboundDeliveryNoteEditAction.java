package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteErrorCode;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteException;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteStockManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/21
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteEditAction extends CommonEditAction {
    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);;
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);

    String oldLifeStatus;
    IObjectData oldObjectData;
    IObjectData newObjectData;
    IObjectDescribe productDescribe;
    List<IObjectData> oldProductObjectDataList = Lists.newArrayList();
    List<IObjectData> newProductObjectDataList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {

        outboundDeliveryNoteManager.modifyArg(actionContext.getTenantId(), arg);
        super.before(arg);
        String recordType = arg.getObjectData().toObjectData().getRecordType();

        oldObjectData = serviceFacade.findObjectData(actionContext.getUser(), objectData.getId(), OutboundDeliveryNoteConstants.API_NAME);
        newObjectData = arg.getObjectData().toObjectData();
        productDescribe = serviceFacade.findObject(actionContext.getTenantId(), OutboundDeliveryNoteProductConstants.API_NAME);

        oldProductObjectDataList = serviceFacade.findDetailObjectDataList(productDescribe, oldObjectData, actionContext.getUser());
        oldLifeStatus = oldObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

        if (!Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "非预设业务类型的出库单不能编辑");
        }

        // 由于OpenAPI的更新操作提交的数据是增量数据，需要与当前的系统数据合并
        newProductObjectDataList = mergeObjectData(arg);

        if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.Ineffective.value)) {
            //校验出库单数量
            outboundDeliveryNoteStockManager.checkProducts(newProductObjectDataList);

            //校验可用库存
            outboundDeliveryNoteStockManager.checkAvailableStock(actionContext.getUser(), arg.getObjectData().toObjectData(), newProductObjectDataList);
        } else {
            checkProductsAndWarehouse();
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.debug("OutboundDeliveryNoteEditActionAfter. result[{}]", result);
        result = super.after(arg, result);
        log.debug("OutboundDeliveryNoteEditAction.after! arg[{}], result[{}]", arg, result);

        IObjectData note = result.getObjectData().toObjectData();

        String newLifeStatus = note.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("OutboundDeliveryNoteEditAction.after! LifeStatus[{}], objectData[{}], result[{}]", newLifeStatus, objectData, result);

        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(note.getId()).operateType(StockOperateTypeEnum.EDIT.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(newLifeStatus)
                .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                .build();

        if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.Ineffective.value)) {
            if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
                //ineffective -> normal 扣减实际库存
                stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
                outboundDeliveryNoteStockManager.minusRealStock(actionContext.getUser(), note, newProductObjectDataList, stockOperateInfo);

            } else if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.UnderReview.value)) {
                //ineffective -> underReview  增加冻结库存
                stockOperateInfo.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
                outboundDeliveryNoteStockManager.addBlockedStock(actionContext.getUser(), note, newProductObjectDataList, false, stockOperateInfo);
            }
        }
        return result;
    }

    private void checkProductsAndWarehouse() {

        if (oldObjectData == null) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "数据不存在");
        }

        if (!Objects.equals(oldObjectData.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class),
                newObjectData.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class))) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "已确认的出库单，仓库不能修改");
        }

        Map<String, BigDecimal> oldProductAmountMap = sumProductAmount(oldProductObjectDataList);
        Map<String, BigDecimal> newProductAmountMap = sumProductAmount(newProductObjectDataList);

        if (oldProductAmountMap.keySet().size() != newProductAmountMap.keySet().size()) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "出库单产品不能修改");
        }

        oldProductAmountMap.keySet().forEach(oldProductId -> {
            if (newProductAmountMap.get(oldProductId) == null || !Objects.equals(oldProductAmountMap.get(oldProductId), newProductAmountMap.get(oldProductId))) {
                throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "出库单产品不能修改");
            }
        });

    }

    private Map<String, BigDecimal> sumProductAmount(List<IObjectData> products) {
        Map<String, BigDecimal> productAmountMap = new HashMap<>();
        products.forEach(product -> {
            String productId = product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class);
            if (productAmountMap.get(productId) == null) {
                productAmountMap.put(productId, product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class));
            } else {
                productAmountMap.put(productId, product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class).add(productAmountMap.get(productId)));
            }
        });
        return productAmountMap;
    }


    private List<IObjectData> mergeObjectData(Arg arg) {
        newObjectData = this.merge(oldObjectData, arg.getObjectData().toObjectData());
        arg.setObjectData(ObjectDataDocument.of(newObjectData));

        Map<String, IObjectData> id2CurrentProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        List<IObjectData> argProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(OutboundDeliveryNoteProductConstants.API_NAME));
        for (IObjectData argProduct : argProductObjectDocList) {
            String outboundDeliveryNoteProductId = Objects.nonNull(argProduct.getId()) ? argProduct.getId() : null;
            boolean isNewAdd = StringUtils.isBlank(outboundDeliveryNoteProductId);
            if (isNewAdd) {
                newProductObjectDataList.add(argProduct);
            } else {
                if (!id2CurrentProductObjectDataMap.containsKey(outboundDeliveryNoteProductId)) {
                    throw new ValidateException("不存在对应出库单产品记录");
                }
                newProductObjectDataList.add(this.merge(id2CurrentProductObjectDataMap.get(outboundDeliveryNoteProductId), argProduct));
            }
        }
        arg.getDetails().remove(OutboundDeliveryNoteProductConstants.API_NAME);
        arg.getDetails().put(OutboundDeliveryNoteProductConstants.API_NAME, ObjectDataDocument.ofList(newProductObjectDataList));
        return newProductObjectDataList;
    }

    private IObjectData merge(IObjectData currentObjectData, IObjectData newObjectDate) {
        Map<String, Object> currentObjectDataMap = ObjectDataExt.of(currentObjectData).toMap();
        Map<String, Object> newObjectDataMap = ObjectDataExt.of(newObjectDate).toMap();
        newObjectDataMap.keySet().forEach(currentObjectDataMap::remove);
        currentObjectDataMap.putAll(newObjectDataMap);
        return ObjectDataExt.of(currentObjectDataMap).getObjectData();
    }
}


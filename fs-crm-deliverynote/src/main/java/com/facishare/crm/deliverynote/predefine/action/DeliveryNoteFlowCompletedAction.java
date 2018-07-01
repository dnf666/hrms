package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.deliverynote.util.ConfigCenter;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
public class DeliveryNoteFlowCompletedAction extends CommonFlowCompletedAction {
    private StockManager stockManager = SpringUtil.getContext().getBean(StockManager.class);
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private DeliveryNoteProductManager deliveryNoteProductManager = SpringUtil.getContext().getBean(DeliveryNoteProductManager.class);

    private String oldStatus;
    private String oldLifeStatus;

    @Override
    protected void before(Arg arg) {
        log.info("DeliveryNoteFlowCompletedAction before, arg:{}", arg);
        super.before(arg);
        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        String statusApiName = DeliveryNoteObjConstants.Field.Status.apiName;
        oldStatus = objectData.get(statusApiName, String.class);
        oldLifeStatus = objectData.get(com.facishare.crm.constants.SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("action before: oldStatus[{}], oldLifeStatus[{}]", oldStatus, oldLifeStatus);
     }

    @Override
    @SuppressWarnings("all")
    protected Result after(Arg arg, Result result) {

        StopWatch stopWatch = StopWatch.create("DeliveryNoteFlowCompletedAction.after" + arg.getDataId());
        log.info("DeliveryNoteFlowCompletedAction after, arg:{}, result:{}", arg, result);
        result = super.after(arg, result);

        if (isRepeatedInvoke) {
            return result;
        }

        IObjectData deliveryNoteObjectData =  deliveryNoteManager.getDeliveryNoteObjectData(arg.getUser(), arg.getDataId());
        String newLifeStatus = deliveryNoteObjectData.get(com.facishare.crm.constants.SystemConstants.Field.LifeStatus.apiName, String.class);

        log.info("oldLifeStatus[{}] -> newLifeStatus[{}]", oldLifeStatus, newLifeStatus);

        if (Objects.equals(ApprovalFlowTriggerType.CREATE.getTriggerTypeCode(), arg.getTriggerType())) {
            if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
                // 新建审核审核通过
                IObjectDescribe productDetailDescribe = serviceFacade.findObject(arg.getTenantId(), DeliveryNoteProductObjConstants.API_NAME);
                List<IObjectData> productObjectDataList = this.serviceFacade.findDetailObjectDataList(productDetailDescribe, deliveryNoteObjectData, arg.getUser());

                boolean isStockEnable = stockManager.isStockEnable(this.getActionContext().getTenantId());
                User user = this.getActionContext().getUser();
                String deliveryWarehouseId = (String) deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName);
                if (isStockEnable && StringUtils.isNotEmpty(deliveryWarehouseId)) {
                    try {
                        deliveryNoteManager.checkWarehouseRealStock(arg.getUser(), deliveryWarehouseId, productObjectDataList);
                    } catch (DeliveryNoteBusinessException e) {
                        log.warn("审批校验实际不通过，直接作废此发货单 deliveryNoteObjectData[{}]", deliveryNoteObjectData, e);
                        deliveryNoteObjectData = deliveryNoteManager.updateStatus(this.getActionContext().getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.INVALID);
                        this.serviceFacade.invalid(deliveryNoteObjectData, this.actionContext.getUser());

                        List<IObjectData> productList = deliveryNoteProductManager.queryObjectDatas(user, Lists.newArrayList(deliveryNoteObjectData.getId()));
                        this.serviceFacade.bulkInvalid(productList, this.actionContext.getUser());
                        return result;
                    }
                }
                deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.HAS_DELIVERED);

                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value)
                        .beforeLifeStatus(com.facishare.crm.constants.SystemConstants.LifeStatus.UnderReview.value)
                        .afterLifeStatus(com.facishare.crm.constants.SystemConstants.LifeStatus.Normal.value)
                        .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                deliveryNoteManager.doCreateDeliveryNoteBecomeHasDelivered(user, deliveryNoteObjectData, productObjectDataList, stockOperateInfo);
            } else {
                // 新建审核审核不通过或撤回
                deliveryNoteManager.updateStatus(actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.UN_DELIVERY);
            }
        }

        if (Objects.equals(ApprovalFlowTriggerType.UPDATE.getTriggerTypeCode(), arg.getTriggerType())) {
            DeliveryNoteObjStatusEnum statusEnum;
            // 有收货日期则为已收货，否则为已发货
            if (Objects.nonNull(deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.ReceiveDate.apiName))) {
                statusEnum = DeliveryNoteObjStatusEnum.RECEIVED;
            } else {
                statusEnum = DeliveryNoteObjStatusEnum.HAS_DELIVERED;
            }
            deliveryNoteManager.updateStatus(actionContext.getUser(), deliveryNoteObjectData, statusEnum);
        }

        if (Objects.equals(ApprovalFlowTriggerType.INVALID.getTriggerTypeCode(), arg.getTriggerType())) {
            if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Invalid.value)) {
                // 作废审批通过
                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(arg.getDataId()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                        .beforeLifeStatus(com.facishare.crm.constants.SystemConstants.LifeStatus.InChange.value)
                        .afterLifeStatus(com.facishare.crm.constants.SystemConstants.LifeStatus.Invalid.value)
                        .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                deliveryNoteManager.invalidDeliveryNote(actionContext.getUser(), deliveryNoteObjectData, oldStatus, stockOperateInfo);
            }
            else if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.InChange.value) && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
                // 作废审批不通过
                deliveryNoteManager.updateStatus(actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.HAS_DELIVERED);
            }
        }

        stopWatch.lap("do biz");

        // 用于测试流程回调重试  todo added by liqiulin   这里测试完了记得删除
        if (ConfigCenter.IS_TEST) {
            try {
                Thread.sleep(ConfigCenter.IS_TEST_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopWatch.lap("test sleep");
        }

        stopWatch.log();
        return result;
    }
}

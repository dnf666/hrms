package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteInvalidAction extends StandardInvalidAction {

    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;
    private RequisitionNoteManager requisitionNoteManager  = SpringUtil.getContext().getBean(RequisitionNoteManager.class);
    private String dataId;
    private String oldLifeStatus;

    @Override
    protected void before(Arg arg) {
        dataId = arg.getObjectDataId();
        IObjectData objectData = requisitionNoteManager.findById(this.actionContext.getUser(), dataId, RequisitionNoteConstants.API_NAME);
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);


        //如果已经确认入库，不允许作废
        Boolean hasTransferredIn = objectData.get(RequisitionNoteConstants.Field.InboundConfirmed.apiName, Boolean.class);
        if (hasTransferredIn) {
            String requisitionName = objectData.getName();
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调拨单" + requisitionName + "已确认入库, 不允许作废");
        }

        super.before(arg);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        User user = actionContext.getUser();

        log.debug("RequisitionNoteInvalidAction.after, arg[{}], actionContext[{}], result[{}]", arg, actionContext, result);

        IObjectData objectData = requisitionNoteManager.findById(this.actionContext.getUser(), dataId, RequisitionNoteConstants.API_NAME);
        log.debug("RequisitionNoteInvalidAction.after, arg[{}], actionContext[{}], resultData[{}]", arg, actionContext, objectData);
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

        //无审批 增加调出仓库实际库存 normal->invalid
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Normal.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
            log.info("goodsReceivedNoteManager.minusRealStock, objectData[{}]", objectData);
            String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

            //作废出库单
            requisitionNoteManager.invalidOutboundDeliveryNote(user, objectData);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                    .operateResult(StockOperateResultEnum.PASS.value)
                    .operateObjectType(StockOperateObjectTypeEnum.REQUISITION_NOTE.value)
                    .build();

            //增加调出仓库实际库存
            requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);
            requisitionNoteCalculateManager.insertOrUpdateStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }
        return result;
    }
}

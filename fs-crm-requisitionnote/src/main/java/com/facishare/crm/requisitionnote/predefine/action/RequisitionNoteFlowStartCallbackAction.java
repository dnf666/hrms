package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.action.CommonFlowStartCallbackAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangk
 * @date 2018/4/12
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteFlowStartCallbackAction extends CommonFlowStartCallbackAction {

    private String oldLifeStatus;
    private RequisitionNoteManager requisitionNoteManager = SpringUtil.getContext().getBean(RequisitionNoteManager.class);
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);
    private String dataId;
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        log.info("RequisitionNoteFlowStartCallbackAction before. arg[{}}", arg);
        dataId = arg.getDataId();
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("RequisitionNoteFlowStartCallbackAction after. objectData[{}}, oldLifeStatus[{}]", objectData, oldLifeStatus);
        result = super.after(arg, result);
        User user = actionContext.getUser();

        if (arg.isTriggerSynchronous()) {
            return result;
        }

        IObjectData objectData = requisitionNoteManager.findById(this.actionContext.getUser(), dataId, RequisitionNoteConstants.API_NAME);
        log.debug("RequisitionNoteFlowStartCallbackAction.after, arg[{}], actionContext[{}], resultData[{}]", arg, actionContext, objectData);
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

        //无审批 增加调出仓库实际库存 normal->invalid
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Normal.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
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
            requisitionNoteCalculateManager.insertOrUpdateStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }
        return result;
    }
}

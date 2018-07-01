package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.action.CommonFlowStartCallbackAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class DeliveryNoteFlowStartCallbackAction extends CommonFlowStartCallbackAction {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private String oldLifeStatus;
    private String oldStatus;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        log.info("arg[{}}", arg);
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        oldStatus = objectData.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
        log.info("oldStatus[{}], oldLifeStatus[{}]", oldLifeStatus, oldLifeStatus);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        log.info("isTriggerSynchronous[{}], arg[{}]", arg.isTriggerSynchronous(), arg);

        // 同步创建流程超时异步回调，不处理业务，详情见：https://www.fxiaoke.com/XV/Home/Index#stream/showfeed/=/id-4216913
        if (!arg.isTriggerSynchronous()) {
            IObjectData objectData = deliveryNoteManager.getObjectDataById(this.actionContext.getUser(), arg.getDataId());
            log.info("DeliveryNoteFlowStartCallbackAction.after, arg[{}], actionContext[{}], resultData[{}]", arg, actionContext, objectData);

            // 批量作废异步处理
            if (Objects.equals(arg.getTriggerType(), ApprovalFlowTriggerType.INVALID.getTriggerTypeCode())) {
                deliveryNoteManager.doAfterInvalidAction(actionContext.getUser(), objectData, oldStatus);
            }
        }

        return result;
    }
}

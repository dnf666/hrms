package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.electronicsign.constants.SystemConstants;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AccountSignCertifyFlowCompletedAction extends CommonFlowCompletedAction {

    private AccountSignCertifyObjManager accountSignCertifyObjManager = SpringUtil.getContext().getBean(AccountSignCertifyObjManager.class);

    @Override
    protected Result after(Arg arg, Result result) {

        result = super.after(arg, result);
        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        String newLifeStatus = objectData.get(com.facishare.crm.constants.SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("newLifeStatus[{}]", newLifeStatus);

        if (Objects.equals(ApprovalFlowTriggerType.CREATE.getTriggerTypeCode(), arg.getTriggerType())
                && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            accountSignCertifyObjManager.reg(actionContext.getUser(), ObjectDataDocument.of(objectData));
        }

        if (Objects.equals(ApprovalFlowTriggerType.UPDATE.getTriggerTypeCode(), arg.getTriggerType())
                && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            accountSignCertifyObjManager.reg(actionContext.getUser(), ObjectDataDocument.of(objectData));
        }

        return result;
    }
}

package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.electronicsign.constants.SystemConstants;
import com.facishare.crm.electronicsign.predefine.manager.InternalSignCertifyUseRangeManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InternalSignCertifyFlowCompletedAction extends CommonFlowCompletedAction {

    private InternalSignCertifyObjManager internalSignCertifyObjManager = SpringUtil.getContext().getBean(InternalSignCertifyObjManager.class);
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager = SpringUtil.getContext().getBean(InternalSignCertifyUseRangeManager.class);

    @Override
    protected Result after(Arg arg, Result result) {

        result = super.after(arg, result);
        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        String newLifeStatus = objectData.get(com.facishare.crm.constants.SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("newLifeStatus[{}]", newLifeStatus);

        if (Objects.equals(ApprovalFlowTriggerType.CREATE.getTriggerTypeCode(), arg.getTriggerType())
                && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            internalSignCertifyObjManager.reg(this.actionContext.getUser(), ObjectDataDocument.of(objectData));
        }

        if (Objects.equals(ApprovalFlowTriggerType.UPDATE.getTriggerTypeCode(), arg.getTriggerType())
                && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            internalSignCertifyObjManager.reg(this.actionContext.getUser(), ObjectDataDocument.of(objectData));
        }

        if (Objects.equals(ApprovalFlowTriggerType.INVALID.getTriggerTypeCode(), arg.getTriggerType())
                && Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Invalid.value)) {
            // 作废审批通过，删除部门使用范围设置
            internalSignCertifyUseRangeManager.deleteByInternalSignCertifyId(objectData.getId());
        }

        return result;
    }
}

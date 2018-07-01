package com.facishare.crm.customeraccount.util;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;

public class FlowUtil {
    public static boolean isCompleted(String oldLifeStatus, String passStatus, int triggerTypeCode) {
        if (ApprovalFlowTriggerType.CREATE.getTriggerTypeCode() == triggerTypeCode) {
            if (StandardFlowCompletedAction.Arg.PASS.equals(passStatus) && SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus)) {
                return true;
            }
            if (!StandardFlowCompletedAction.Arg.PASS.equals(passStatus) && SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
                return true;
            }
        } else if (ApprovalFlowTriggerType.INVALID.getTriggerTypeCode() == triggerTypeCode) {
            if (StandardFlowCompletedAction.Arg.PASS.equals(passStatus) && SystemConstants.LifeStatus.Invalid.value.equals(oldLifeStatus)) {
                return true;
            }
            if (!StandardFlowCompletedAction.Arg.PASS.equals(passStatus) && SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus)) {
                return true;
            }
        }
        return false;
    }
}

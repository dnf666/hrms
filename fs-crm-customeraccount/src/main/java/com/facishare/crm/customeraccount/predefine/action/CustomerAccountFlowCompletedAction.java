package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;

public class CustomerAccountFlowCompletedAction extends StandardFlowCompletedAction {
    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }
}
package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;

public class RebateOutcomeDetailBulkInvalidAction extends StandardBulkInvalidAction {

    @Override
    public void before(StandardBulkInvalidAction.Arg arg) {
        throw new ValidateException("不支持该操作");
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }
}

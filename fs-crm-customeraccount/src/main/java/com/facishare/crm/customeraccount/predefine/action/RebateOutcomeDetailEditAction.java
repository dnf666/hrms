package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;

public class RebateOutcomeDetailEditAction extends StandardEditAction {

    @Override
    protected void before(Arg arg) {
        throw new ValidateException("不支持该操作");
    }
    
}

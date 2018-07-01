package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;

public class RebateOutcomeDetailInvalidAction extends StandardInvalidAction {
    
    @Override
    public void before(Arg arg) {
       throw new ValidateException("不支持该操作");
    }
}

package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.predef.action.StandardLockAction;

/**
 * 使用标准操作不需要定制<br>
 * Created by xujf on 2017/10/25.
 */
public class CustomerAccountLockAction extends StandardLockAction {
    @Override
    protected void doFunPrivilegeCheck() {

    }

    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }
}

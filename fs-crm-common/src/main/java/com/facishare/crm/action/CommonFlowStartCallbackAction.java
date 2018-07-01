package com.facishare.crm.action;

import com.facishare.paas.appframework.core.predef.action.StandardFlowStartCallbackAction;

/**
 * @author liangk
 * @date 22/05/2018
 */
public class CommonFlowStartCallbackAction extends StandardFlowStartCallbackAction {

    @Override
    protected Result doAct(Arg arg) {
        if (!arg.isTriggerSynchronous()) {
            return super.doAct(arg);
        } else {
            return new Result(true);
        }

    }
}

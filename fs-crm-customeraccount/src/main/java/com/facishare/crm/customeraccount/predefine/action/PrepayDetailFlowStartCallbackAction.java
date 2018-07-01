package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.paas.appframework.core.predef.action.StandardFlowStartCallbackAction;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrepayDetailFlowStartCallbackAction extends StandardFlowStartCallbackAction {
    private PrepayDetailManager prepayDetailManager;
    private String oldLifeStatus;

    @Override
    protected void before(Arg arg) {

        super.before(arg);
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
    }

    @Override
    protected Result doAct(Arg arg) {
        if (arg.isTriggerSynchronous()) {
            //如果审批流回调且为同步审批流触发时，直接跳过，不走更新逻辑
            return new Result(true);
        }
        super.doAct(arg);
        log.info("callback objectData:{},lifeStatus:{},arg:{}", objectData, oldLifeStatus, arg);
        prepayDetailManager.updateBalance(actionContext.getUser(), objectData, oldLifeStatus);
        return new Result(true);
    }
}

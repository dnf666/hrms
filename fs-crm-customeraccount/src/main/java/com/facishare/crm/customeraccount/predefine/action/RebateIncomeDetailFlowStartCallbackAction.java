package com.facishare.crm.customeraccount.predefine.action;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardFlowStartCallbackAction;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailFlowStartCallbackAction extends StandardFlowStartCallbackAction {
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private String oldLifeStatus = null;
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;

    @Override
    protected void before(Arg arg) {
        rebateOutcomeDetailManager = SpringUtil.getContext().getBean(RebateOutcomeDetailManager.class);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        super.before(arg);
        boolean hasRebateOutcome = rebateOutcomeDetailManager.hasRebateOutcomeDetails(actionContext.getUser(), objectData.getId());
        if (hasRebateOutcome) {
            log.warn("FlowStartCallback有时间间隔，返利收入绑定了支出，不可作废");
            throw new ValidateException("已生成支出明细,不可作废");
        }
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
    }

    @Override
    protected Result doAct(Arg arg) {
        if (arg.isTriggerSynchronous()) {
            //如果审批流回调且为同步审批流触发时，直接跳过，不走更新逻辑
            return new Result(true);
        }
        super.doAct(arg);
        objectData = serviceFacade.findObjectDataIncludeDeleted(actionContext.getUser(), arg.getDataId(), objectDescribe.getApiName());
        Date start = objectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date end = objectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("callback objectData:{},lifeStatus:{},arg:{}", objectData, oldLifeStatus, arg);
        if (ObjectDataUtil.isCurrentTimeActive(start, end) && StringUtils.isNotEmpty(lifeStatus)) {
            log.info("RebateIncomeDetailFlowStartCallbackAction.rebateIncomeDetailManager.updateBalanceForLifeStatus");
            rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), objectData, oldLifeStatus, lifeStatus);
        }
        return new Result(true);
    }
}

package com.facishare.crm.customeraccount.predefine.action;

import java.util.Date;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.NewStandardFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.util.FlowUtil;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailFlowCompletedAction extends NewStandardFlowCompletedAction {
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private String oldLifeStatus = null;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        IObjectData objectData = serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
    }

    @Override
    protected Result doAct(Arg arg) {
        Result result = super.doAct(arg);
        Date startTime = resultData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date endTime = resultData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        String newLifeStatus = resultData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.debug("RebateIncomeDetailFlowCompletedAction,arg:{},oldLifeStatus:{},data:{}", arg.toString(), oldLifeStatus, resultData.toJsonString());
        if (FlowUtil.isCompleted(oldLifeStatus, arg.getStatus(), arg.getTriggerType())) {
            return result;
        }
        //判断是否更新客户账户，有效期内更新，按没有流程处理（因为返利审批的时候没有更新客户账户锁定的）isFromFlowOperation=false
        if (ObjectDataUtil.isCurrentTimeActive(startTime, endTime) && !arg.getApprovalFlowTriggerType().getId().equals(ApprovalFlowTriggerType.UPDATE.getId())) {
            resultData = serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
            rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), resultData, oldLifeStatus, newLifeStatus);
        }
        return result;
    }

}

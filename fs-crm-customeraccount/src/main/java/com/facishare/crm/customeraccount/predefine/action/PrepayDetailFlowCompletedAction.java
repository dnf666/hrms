package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.NewStandardFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.util.FlowUtil;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrepayDetailFlowCompletedAction extends NewStandardFlowCompletedAction {
    private CustomerAccountManager customerAccountManager;
    private String oldLifeStatus = null;
    private PrepayDetailManager prepayDetailManager;

    @Override
    protected void before(Arg arg) {
        String id = arg.getDataId();
        long startTime = System.currentTimeMillis();
        super.before(arg);
        log.debug("before1 id={},spendTime={}ms", id, (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        IObjectData objectData = serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.debug("before2 id={},spendTime={}ms", id, (System.currentTimeMillis() - startTime));
    }

    @Override
    protected Result doAct(Arg arg) {
        Result result = super.doAct(arg);
        String newLifeStatus = resultData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.debug("PrepayDetailFlowCompletedAction,arg:{},ApprovalType:{},oldLifeStatus:{},data:{}", arg.toString(), ApprovalFlowTriggerType.getType(arg.getTriggerType()), oldLifeStatus, resultData.toJsonString());
        if (FlowUtil.isCompleted(oldLifeStatus, arg.getStatus(), arg.getTriggerType())) {
            return result;
        }
        resultData.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
        //需要的使用才取容器里取bean<br>
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        if (!arg.getApprovalFlowTriggerType().getId().equals(ApprovalFlowTriggerType.UPDATE.getId())) {
            prepayDetailManager.updateBalance(actionContext.getUser(), resultData, oldLifeStatus);
        }
        return result;
    }
}

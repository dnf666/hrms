package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.FlowStandardAddAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.util.DateUtil;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailAddAction extends FlowStandardAddAction {
    private CustomerAccountManager customerAccountManager;
    private RebateIncomeDetailManager rebateIncomeDetailManager;

    @Override
    protected void modifyObjectDataBeforeCreate(IObjectData objectData, IObjectDescribe describe) {
        String oldLifeStatus = (String) this.arg.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        super.modifyObjectDataBeforeCreate(objectData, describe);
        if (StringUtil.isNullOrEmpty(oldLifeStatus)) {
            this.objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Ineffective.value);
        } else {
            this.objectData.set(SystemConstants.Field.LifeStatus.apiName, oldLifeStatus);
        }
    }

    @Override
    public void before(Arg arg) {
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        super.before(arg);
        BigDecimal amount = objectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new ValidateException("返利金额必须大于0");
        }
        String customerId = objectData.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
        IObjectData customerAccountObjectData = customerAccountManager.getCustomerAccountByCustomerId(actionContext.getUser(), customerId);
        String customerAccountId = customerAccountObjectData.getId();
        objectData.set(RebateIncomeDetailConstants.Field.CustomerAccount.apiName, customerAccountId);
        String fsUserId = actionContext.getUser().getUserId();
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(fsUserId));
        objectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, 0.00);
        objectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, 0.00);
        //为null 的话不能进行Date.class强制转换
        Object startTime = objectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName);
        Object endTime = objectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName);
        //默认开始时间为当天
        if (startTime == null || startTime.toString().equals("0")) {
            objectData.set(RebateIncomeDetailConstants.Field.StartTime.apiName, DateUtil.getNowBenginTime());
        }
        //默认结束时间为10年
        if (endTime == null || endTime.toString().equals("0")) {
            long endTimeStr = DateUtil.getNowBenginDateTime().plusYears(10).getMillis();
            objectData.set(RebateIncomeDetailConstants.Field.EndTime.apiName, endTimeStr);
        }

        log.debug("rebateIncomeObj to be save,for rebateObj:{}", objectData);
    }

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        IObjectData resultData = result.getObjectData().toObjectData();
        String lifeStatus = resultData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (RequestUtil.isFromInner(actionContext)) {
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                resultData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                resultData = rebateIncomeDetailManager.updateTmp(actionContext.getUser(), resultData, SystemConstants.Field.LockStatus.apiName);
            }
        }

        Date startTime = objectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date endTime = objectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        log.debug("rebateIncomeObj saved result,for rebateObj:{}", resultData);
        if (ObjectDataUtil.isCurrentTimeActive(startTime, endTime)) {
            rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), resultData, SystemConstants.LifeStatus.Ineffective.value, lifeStatus);
        }
        return result;
    }

    @Override
    public Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        if (!RequestUtil.isFromInner(actionContext)) {
            Map<String, ApprovalFlowStartResult> startApprovalFlowResult = super.startApprovalFlow(objectDataList, approvalFlowTriggerType, updatedFieldMap);
            ApprovalFlowStartResult objectDataApprovalFlowStartResult = startApprovalFlowResult.get(objectData.getId());
            log.debug("objectDataApprovalFlowStartResult:{}", objectDataApprovalFlowStartResult);
            String lifeStatus = null;
            if (ApprovalFlowStartResult.SUCCESS.equals(objectDataApprovalFlowStartResult)) {
                lifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            } else if (ApprovalFlowStartResult.APPROVAL_NOT_EXIST.equals(objectDataApprovalFlowStartResult)) {
                lifeStatus = SystemConstants.LifeStatus.Normal.value;
            } else if (ApprovalFlowStartResult.ALREADY_EXIST.equals(objectDataApprovalFlowStartResult)) {
                lifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            } else if (ApprovalFlowStartResult.FAILED.equals(objectDataApprovalFlowStartResult)) {
                lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
            }
            objectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            return startApprovalFlowResult;
        }
        return Maps.newHashMap();
    }
}

package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.flow.exception.StartApprovalFlowException;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * 1.如果已经有关联支出的话则不允许修改amount<br>
 * 2.如果是手动创建收入明细可以更新值，比如以前old值是50 我更新为80，那么我们取更新客户账户的时候会给余额加 (80-50)<br>
 * 3.如果自动创建收入明细则不允许修改，通过是否有退款id来判断<br>
 */
@Slf4j
public class RebateIncomeDetailEditAction extends StandardEditAction {
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private String oldLifeStatus = null;
    private boolean oldActive;
    private BigDecimal oldAmount;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        rebateOutcomeDetailManager = SpringUtil.getContext().getBean(RebateOutcomeDetailManager.class);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        IObjectData dbData = serviceFacade.findObjectData(actionContext.getTenantId(), this.objectData.getId(), this.objectDescribe);
        checkReadOnlyField(dbData, objectData);
        BigDecimal newAmount = this.objectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        oldAmount = dbData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        oldLifeStatus = dbData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String refundId = ObjectDataUtil.getReferenceId(dbData, RebateIncomeDetailConstants.Field.Refund.apiName);
        if (StringUtils.isNotEmpty(refundId)) {
            throw new ValidateException("返利关联了退款，不能进行编辑");
        }
        if (SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus) && oldAmount.compareTo(newAmount) != 0) {
            log.info("nochange flowComplete, arg={}", arg);
            throw new ValidateException(String.format("[%s]状态禁止编辑金额", SystemConstants.LifeStatus.Normal.label));
        }
        String idStr = this.objectData.getId();
        boolean hasOutcome = rebateOutcomeDetailManager.hasRebateOutcomeDetails(actionContext.getUser(), idStr);
        //如果返利收入被使用则不能修改总额(amout)，而amount唯一修改渠道是页面（即如果页面传过来的amount!=(数据库中的)dbamount，则可以判断是页面传过来的数据）。
        if (hasOutcome && oldAmount.compareTo(newAmount) != 0) {
            log.error("收入记录已关联支出，不允许页面修改金额,for incomeId:{},oldAmount:{},newAmount:{}", idStr, oldAmount, newAmount);
            throw new ValidateException("收入记录已关联支出，禁止修改金额");
        }
        Date start = dbData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date end = dbData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        oldActive = ObjectDataUtil.isCurrentTimeActive(start, end);
    }

    private void checkReadOnlyField(IObjectData oldObjetData, IObjectData newObjectData) {
        BigDecimal oldUsedRebate = ObjectDataUtil.getBigDecimal(oldObjetData, RebateIncomeDetailConstants.Field.UsedRebate.apiName);//toBigDecimal(oldUsedRebateObject);
        BigDecimal oldAvailableRebate = ObjectDataUtil.getBigDecimal(oldObjetData, RebateIncomeDetailConstants.Field.AvailableRebate.apiName);//toBigDecimal(oldAvailableRebateObject);

        BigDecimal newUsedRebate = ObjectDataUtil.getBigDecimal(newObjectData, RebateIncomeDetailConstants.Field.UsedRebate.apiName);//toBigDecimal(newUsedRebateObject);
        BigDecimal newAvailableRebate = ObjectDataUtil.getBigDecimal(newObjectData, RebateIncomeDetailConstants.Field.AvailableRebate.apiName);//toBigDecimal(newAvailableRebateObject);

        List<String> fields = Lists.newArrayList();
        if (oldAvailableRebate.compareTo(newAvailableRebate) != 0) {
            fields.add(RebateIncomeDetailConstants.Field.AvailableRebate.label);
        }
        if (oldUsedRebate.compareTo(newUsedRebate) != 0) {
            fields.add(RebateIncomeDetailConstants.Field.UsedRebate.label);
        }
        if (CollectionUtils.isNotEmpty(fields)) {
            throw new ValidateException(Joiner.on(",").join(fields).concat("不可编辑").replaceAll("\\(元\\)", ""));
        }
    }

    private BigDecimal toBigDecimal(Object bigDecimalObject) {
        if (bigDecimalObject == null || String.valueOf(bigDecimalObject).length() == 0) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(String.valueOf(bigDecimalObject));
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        if (RequestUtil.isFromInner(actionContext)) {
            result = doFromInner(arg, result);
        } else {
            result = doFromCep(arg, result);
        }
        IObjectData incomeObjectData = result.getObjectData().toObjectData();
        rebateIncomeDetailManager.updateRebateIncomeBalanceWhenEdit(actionContext.getUser(), incomeObjectData, oldLifeStatus, oldAmount, oldActive);
        return result;
    }

    private Result doFromInner(Arg arg, Result result) {
        //TODO super.after需要跳过流程
        result = super.after(arg, result);
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        IObjectData resultObjectData = result.getObjectData().toObjectData();
        if (SystemConstants.LifeStatus.UnderReview.value.equals(newLifeStatus)) {
            resultObjectData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            rebateIncomeDetailManager.update(actionContext.getUser(), resultObjectData);
        } else if (SystemConstants.LifeStatus.Normal.value.equals(newLifeStatus) && SystemConstants.LifeStatus.Ineffective.equals(oldLifeStatus)) {
            this.serviceFacade.startWorkFlow(resultObjectData.getId(), resultObjectData.getDescribeApiName(), 1, this.actionContext.getUser(), Maps.newHashMap());
        }
        return result;
    }

    private Result doFromCep(Arg arg, Result result) {
        result = super.after(arg, result);
        IObjectData objectData = result.getObjectData().toObjectData();
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
            Map<String, ApprovalFlowStartResult> approvalResultMap = this.serviceFacade.batchStartApproval(ApprovalFlowTriggerType.CREATE, actionContext.getUser(), Lists.newArrayList(objectData), Maps.newHashMap());
            ApprovalFlowStartResult approvalFlowStartResult = approvalResultMap.get(objectData.getId());
            if (ApprovalFlowStartResult.ALREADY_EXIST.equals(approvalFlowStartResult)) {
                newLifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            } else if (ApprovalFlowStartResult.APPROVAL_NOT_EXIST.equals(approvalFlowStartResult)) {
                newLifeStatus = SystemConstants.LifeStatus.Normal.value;
            } else if (ApprovalFlowStartResult.SUCCESS.equals(approvalFlowStartResult)) {
                newLifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            } else if (ApprovalFlowStartResult.FAILED.equals(approvalFlowStartResult)) {
                log.warn("Start Approval Fialed,User:{},ObjectData:{},ApprovalType:{}", actionContext.getUser(), objectData.toJsonString(), ApprovalFlowTriggerType.CREATE);
                throw new StartApprovalFlowException("开启流程失败");
            }
        }
        objectData.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
        return result;
    }

}

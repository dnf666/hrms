package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.flow.exception.StartApprovalFlowException;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 编辑大致分以下两种场景：<br>
 * 1).关联回款： 提交 -> 驳回 （金额相当于释放了） -> 单独编辑预存款(因为此时预存款是没有审批流的，此时状态还是未生效)。<br>
 * 2).不关联回款：提交 -> 驳回 (金额相当于释放了) -> 再编辑预存款（会触发审批流）<br>
 * 3).不关联回款：对于手动创建预存款且没有审批流程的情况，创建了预存款立马变成normal的状态，此时再编辑不用变更余额。<br>
 * 4)关联回款：如果回款里改了金额，回款触发了审批流怎么办？ newLifeStatus  = underReview
 *
 * 所以大体的判断逻辑是：<br>
 * 1).if (ineffective && (paymentId ==null ) && (refundId == null))
 * {
 *     1.触发审批流 -> 变更客户账户余额(是触发审批流才导致变更金额)。
 * }
 */
@Slf4j
public class PrepayDetailEditAction extends StandardEditAction {

    private PrepayDetailManager prepayDetailManager;
    private CustomerAccountManager customerAccountManger;
    private String newLifeStatus = null;
    private String oldLifeStatus = null;
    private BigDecimal oldAmount;

    private boolean needUpdateCustomerAccountBalance = false;

    @Override
    public void before(Arg arg) {
        //任何状态都可以进行编辑（备注），<br>
        customerAccountManger = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        super.before(arg);
        IObjectData oldObjectData = serviceFacade.findObjectData(actionContext.getUser(), objectData.getId(), objectData.getDescribeApiName());

        String paymentId = oldObjectData.get(PrepayDetailConstants.Field.OrderPayment.apiName, String.class);
        String refundId = oldObjectData.get(PrepayDetailConstants.Field.Refund.apiName, String.class);
        if (StringUtils.isNotBlank(paymentId)) {
            log.warn("预存款明细关联了回款不能进行编辑,for prepayDetail id:{}", oldObjectData.getId());
            throw new ValidateException("预存款明细关联了回款，不能进行编辑");
        }
        if (StringUtils.isNotBlank(refundId)) {
            log.warn("预存款明细关联了退款不能进行编辑,for prepayDetail id:{}", oldObjectData.getId());
            throw new ValidateException("预存款明细关联了退款，不能进行编辑");
        }

        oldLifeStatus = oldObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        oldAmount = oldObjectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
        if (RequestUtil.isFromInner(actionContext)) {
            //有可能回款改变了金额，回款触发了审批流，那么回款的lifestatus就会有变化。<br>
            newLifeStatus = arg.getObjectData().toObjectData().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        }
        BigDecimal newAmount = arg.getObjectData().toObjectData().get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
        if (SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus) && oldAmount.compareTo(newAmount) != 0) {
            log.info("nochange flowComplete, arg={}", arg);
            throw new ValidateException(String.format("[%s]状态禁止编辑金额", SystemConstants.LifeStatus.Normal.label));
        }
        //校验一下客户账户预存款余额是否够，手动编辑预存款明细会重新触发审批流，所以这里需要进行余额校验<br>
        String outcomeType = objectData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
        if (oldAmount.compareTo(newAmount) != 0 && !StringUtil.isNullOrEmpty(outcomeType)) {
            String customerId = ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Customer.apiName);
            IObjectData customerAccountObjectData = customerAccountManger.getCustomerAccountByCustomerId(actionContext.getUser(), customerId);
            BigDecimal amount = objectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
            log.debug("Before,Prepay Edit Action,customerAccountObjectData:{}", customerAccountObjectData.toJsonString());
            BigDecimal prepayAvailableBalance = customerAccountObjectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
            if (prepayAvailableBalance.compareTo(amount) < 0) {
                log.info(String.format("客户账户[%s]预存款可用余额不足", customerAccountObjectData.getId()));
                throw new ValidateException(String.format("客户账户[%s]预存款可用余额不足", customerAccountObjectData.getName()));
            }
        }
        log.debug("Before Edit PrepayTransactionDetail,Old ObjectData:{},To Update ObjectData:{}", objectData.toJsonString(), oldObjectData.toJsonString());
    }

    @Override
    protected Result after(Arg arg, Result result) {
        if (RequestUtil.isFromInner(actionContext)) {
            result = doFromInner(arg, result);
        } else {
            result = doFromCep(arg, result);
        }
        if (needUpdateCustomerAccountBalance) {
            log.info("begin to update prepayBalanceWhenEdit,for objectData:{}", arg.getObjectData());
            IObjectData updatedObjectData = result.getObjectData().toObjectData();
            String id = updatedObjectData.getId();
            BigDecimal newAmount = updatedObjectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
            String incomeType = objectData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
            String outcomeType = objectData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
            String customerId = objectData.get(PrepayDetailConstants.Field.Customer.apiName, String.class);
            String customerAccountId = objectData.get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class);
            prepayDetailManager.updatePrepayBalanceWhenEdit(actionContext.getUser(), customerId, incomeType, outcomeType, oldLifeStatus, newLifeStatus, oldAmount, newAmount, id, customerAccountId);
        }
        return result;
    }

    private Result doFromInner(Arg arg, Result result) {
        //接口过来，after里需要跳过流程
        result = super.after(arg, result);
        IObjectData resultObjectData = result.getObjectData().toObjectData();
        if (SystemConstants.LifeStatus.UnderReview.value.equals(newLifeStatus)) {
            resultObjectData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            needUpdateCustomerAccountBalance = true;
            log.info("doFromInner--->newLifeStatus:{},for prepayDetailObject:{}", newLifeStatus, arg.getObjectData());
            prepayDetailManager.update(actionContext.getUser(), resultObjectData);
        } else if (SystemConstants.LifeStatus.Normal.value.equals(newLifeStatus) && SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
            this.serviceFacade.startWorkFlow(resultObjectData.getId(), resultObjectData.getDescribeApiName(), 1, this.actionContext.getUser(), Maps.newHashMap());
        }
        return result;
    }

    /**
     * 页面过来<br>
     * @param arg
     * @param result
     * @return
     */
    private Result doFromCep(Arg arg, Result result) {
        result = super.after(arg, result);
        if (SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
            Map<String, ApprovalFlowStartResult> approvalResultMap = this.serviceFacade.batchStartApproval(ApprovalFlowTriggerType.CREATE, this.actionContext.getUser(), Lists.newArrayList(objectData), Maps.newHashMap());
            if (approvalResultMap.containsValue(ApprovalFlowStartResult.FAILED)) {
                throw new StartApprovalFlowException("对象触发审批流失败");
            }
            ApprovalFlowStartResult approvalFlowStartResult = approvalResultMap.get(objectData.getId());
            if (ApprovalFlowStartResult.SUCCESS.equals(approvalFlowStartResult) || ApprovalFlowStartResult.ALREADY_EXIST.equals(approvalFlowStartResult)) {
                log.info("start approval success,for id:{}", objectData.getId());
                needUpdateCustomerAccountBalance = true;
                newLifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            } else if (ApprovalFlowStartResult.APPROVAL_NOT_EXIST.equals(approvalFlowStartResult)) {
                newLifeStatus = SystemConstants.LifeStatus.Normal.value;
            } else if (ApprovalFlowStartResult.APPROVAL_FILTER_EXCEPTION.equals(approvalFlowStartResult)) {
                //暂不处理
            } else if (ApprovalFlowStartResult.ALREADY_EXIST.equals(approvalFlowStartResult)) {
                log.info("approval already exist,for id:{}", objectData.getId());
                needUpdateCustomerAccountBalance = true;
                newLifeStatus = SystemConstants.LifeStatus.UnderReview.value;
            }
        } else if (SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus)) {
            newLifeStatus = SystemConstants.LifeStatus.Normal.value;
        }
        return result;
    }

}

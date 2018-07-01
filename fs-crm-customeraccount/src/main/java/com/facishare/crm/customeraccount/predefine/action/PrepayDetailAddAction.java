package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.FlowStandardAddAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
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
public class PrepayDetailAddAction extends FlowStandardAddAction {
    private CustomerAccountManager customerAccountManger;
    private PrepayDetailManager prepayDetailManager;

    @Override
    public Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        if (RequestUtil.isFromInner(actionContext)) {
            return Maps.newHashMap();
        }
        Map<String, ApprovalFlowStartResult> startApprovalFlowResult = super.startApprovalFlow(objectDataList, approvalFlowTriggerType, updatedFieldMap);
        ApprovalFlowStartResult objectDataApprovalFlowStartResult = startApprovalFlowResult.get(objectData.getId());
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
        log.info("doFlow->lifestatus:{}", lifeStatus);
        objectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
        return startApprovalFlowResult;
    }

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
        customerAccountManger = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        super.before(arg);
        String customerId = ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Customer.apiName);
        if (Objects.isNull(customerId)) {
            throw new ValidateException("客户ID不能为空");
        }
        BigDecimal amount = objectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
        if (Objects.isNull(amount)) {
            throw new ValidateException("金额不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidateException("金额不能为负数");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidateException("金额不能为0");
        }
        IObjectData customerAccountObjectData = customerAccountManger.getCustomerAccountByCustomerId(actionContext.getUser(), customerId);

        log.debug("Before,Prepay Add Action,customerAccountObjectData:{}", customerAccountObjectData.toJsonString());
        String outcomeType = objectData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
        if (!StringUtil.isNullOrEmpty(outcomeType)) {
            BigDecimal prepayAvailableBalance = customerAccountObjectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
            if (prepayAvailableBalance.compareTo(amount) < 0) {
                log.info(String.format("客户账户[%s]预存款可用余额不足", customerAccountObjectData.getId()));
                throw new ValidateException(String.format("客户账户[%s]预存款可用余额不足", customerAccountObjectData.getName()));
            }
        }
        String customerAccountId = customerAccountObjectData.getId();
        objectData.set(PrepayDetailConstants.Field.CustomerAccount.apiName, customerAccountId);
        String fsUserId = actionContext.getUser().getUserId();
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(fsUserId));
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        log.debug("prepayDetailAddAction result:{}", result);
        if (result.getObjectData() != null) {
            result.getObjectData().remove("relevant_team");
        }
        IObjectData resultData = result.getObjectData().toObjectData();
        String lifeStatus = resultData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (RequestUtil.isFromInner(actionContext)) {
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                resultData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                prepayDetailManager.update(actionContext.getUser(), resultData);
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                this.serviceFacade.startWorkFlow(resultData.getId(), resultData.getDescribeApiName(), 1, this.actionContext.getUser(), Maps.newHashMap());
            }
        }
        //APPROVAL_FILTER_EXCEPTION时为null
        if (StringUtils.isNotEmpty(lifeStatus)) {
            log.info("begin to update customerAccount prepaybalance,for prepayObj.id:{}, user:{}", result.getObjectData().toObjectData().getId(), actionContext.getUser());
            prepayDetailManager.updateBalance(actionContext.getUser(), resultData, SystemConstants.LifeStatus.Ineffective.value);
        }
        return result;
    }
}

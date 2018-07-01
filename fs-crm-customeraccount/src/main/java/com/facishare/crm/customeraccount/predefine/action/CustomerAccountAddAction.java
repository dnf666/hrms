package com.facishare.crm.customeraccount.predefine.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.FlowStandardAddAction;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerAccountAddAction extends FlowStandardAddAction {

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

    /**
     *客户账户永远不需要开启流程
     * @param objectDataList
     * @return
     */
    @Override
    public Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        /**
         * 如果是service过来的，则不用开启审批流<br>
         */
        if (RequestUtil.isFromInner(actionContext)) {
            return new HashMap<String, ApprovalFlowStartResult>();
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
    protected void before(Arg arg) {
        super.before(arg);
        String customerId = ObjectDataUtil.getReferenceId(objectData, CustomerAccountConstants.Field.Customer.apiName);
        IObjectData customerAccountData = getCustomerAccountByCustomerId(customerId);

        if (customerAccountData != null) {
            log.info("客户账户已经初始化，for customerId:{}", customerId);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_ACCOUNT_INIT_READY, CustomerAccountErrorCode.CUSTOMER_ACCOUNT_INIT_READY.getMessage());
        }
        String fsUserId = actionContext.getUser().getUserId();
        objectData.set(CustomerAccountConstants.Field.PrepayBalance.apiName, "0");
        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, "0");
        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, "0");
        objectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, "0");
        objectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, "0");
        objectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, "0");
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(fsUserId));
    }

    private IObjectData getCustomerAccountByCustomerId(String customerId) {
        IFilter filter = new Filter();
        String apiName = CustomerAccountConstants.Field.Customer.getApiName();
        filter.setFieldName(apiName);
        filter.setFieldValues(Lists.newArrayList(customerId));
        filter.setOperator(Operator.EQ);
        List<IObjectData> dataList = serviceFacade.findDataWithWhere(actionContext.getUser(), CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, 10);
        if (CollectionUtils.empty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }

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

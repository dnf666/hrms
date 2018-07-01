package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailInvalidAction extends StandardInvalidAction {
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    private CustomerAccountManager customerAccountManager;
    private String lifeStatus = null;
    private String oldLifeStatus = null;
    private IObjectData resultObjectData;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        rebateOutcomeDetailManager = SpringUtil.getContext().getBean(RebateOutcomeDetailManager.class);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        Optional<IObjectData> rebateIncomeObjectData = objectDataList.stream().filter(o -> o.getId().equals(arg.getObjectDataId())).findAny();
        String customerId = ObjectDataUtil.getReferenceId(rebateIncomeObjectData.orElseThrow(() -> new ValidateException("客户账户不存在")), RebateIncomeDetailConstants.Field.Customer.apiName);
        Optional<IObjectData> customerAccouontObjectData = customerAccountManager.getCustomerAccountIncludeInvalidByCustomerId(actionContext.getUser(), customerId);
        String lifeStatus = customerAccouontObjectData.get().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        boolean isDeleted = customerAccouontObjectData.get().isDeleted();
        if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus) || isDeleted) {
            throw new ValidateException("客户和客户账户已作废，该回款已封存，无法作废！");
        }
        channelValidate(rebateIncomeObjectData.get());
        oldLifeStatus = rebateIncomeObjectData.get().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (SystemConstants.LifeStatus.UnderReview.value.equals(oldLifeStatus)) {
            throw new ValidateException("审核中不可作废");
        }
        checkAvailableRebate(rebateIncomeObjectData.get());
        boolean hasRebateOutcome = rebateOutcomeDetailManager.hasRebateOutcomeDetails(actionContext.getUser(), arg.getObjectDataId());
        if (hasRebateOutcome) {
            throw new ValidateException("已生成支出明细,不可作废");
        }
        if (actionContext.getAttributes() != null) {
            lifeStatus = actionContext.getAttribute("lifeStatus");
        }
    }

    private void channelValidate(IObjectData objectData) {
        //对于页面过来的请求，不能作废由退款创建的返利收入明细<br>
        if (!RequestUtil.isFromInner(actionContext)) {
            String refundId = objectData.get(RebateIncomeDetailConstants.Field.Refund.apiName, String.class);
            if (StringUtils.isNotBlank(refundId)) {
                log.warn("不能手动作废由退款创建的返利收入明细,for rebateincome id:{}, refundId:{}.", objectData.getId(), refundId);
                throw new ValidateException("由退款关联创建的返利记录，暂不支持作废！");
            }
        }
    }

    private void checkAvailableRebate(IObjectData rebateIncomeObjectData) {
        String lifeStatus = rebateIncomeObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
            return;
        }
        String customerId = ObjectDataUtil.getReferenceId(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.Customer.apiName);
        BigDecimal rebateIncomeAmount = rebateIncomeObjectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        IObjectData customerAccountData = customerAccountManager.getCustomerAccountByCustomerId(actionContext.getUser(), customerId);
        BigDecimal rebateAvailable = customerAccountData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
        if (rebateIncomeAmount.compareTo(rebateAvailable) > 0) {
            throw new ValidateException(String.format("客户账户[%s]可用返利余额不足，禁止作废收入", customerAccountData.getName()));
        }
    }

    @Override
    protected Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        Map<String, ApprovalFlowStartResult> result = null;
        if (RequestUtil.isFromInner(actionContext)) {
            List<IObjectData> detailsObjectData = objectDataList.stream().filter(objectData -> !objectDescribe.getApiName().equals(objectData.getDescribeApiName())).collect(Collectors.toList());
            result = super.startApprovalFlow(detailsObjectData, approvalFlowTriggerType, updatedFieldMap);
            log.debug("arg:{},details:{}", arg, JsonUtil.toJson(result));
            for (IObjectData data : objectDataList) {
                if (!data.getId().equals(arg.getObjectDataId())) {
                    continue;
                }
                if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                    result.put(data.getId(), ApprovalFlowStartResult.SUCCESS);
                    data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                    IObjectData resultData = rebateIncomeDetailManager.update(actionContext.getUser(), data);
                    data.setVersion(resultData.getVersion());
                    data.setLastModifiedTime(resultData.getLastModifiedTime());
                    data.setLastModifiedBy(resultData.getLastModifiedBy());
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                    rebateIncomeDetailManager.update(actionContext.getUser(), data);
                    result.put(data.getId(), ApprovalFlowStartResult.APPROVAL_NOT_EXIST);
                }
            }
        } else {
            result = super.startApprovalFlow(objectDataList, approvalFlowTriggerType, updatedFieldMap);
        }
        return result;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        ObjectDataDocument resultObject = null;
        if (RequestUtil.isFromInner(actionContext)) {
            //内部调用直接更新状态
            List<IObjectData> inChangeObjectDataList = Lists.newArrayList();
            for (IObjectData argObjectData : objectDataList) {
                argObjectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                    argObjectData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                    inChangeObjectDataList.add(argObjectData);
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    if (argObjectData.getId().equals(arg.getObjectDataId())) {
                        resultObject = ObjectDataDocument.of(argObjectData);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(inChangeObjectDataList)) {
                List<IObjectData> list = rebateIncomeDetailManager.batchUpdate(actionContext.getUser(), inChangeObjectDataList);
                for (IObjectData data : list) {
                    if (data.getId().equals(arg.getObjectDataId())) {
                        resultObject = ObjectDataDocument.of(data);
                    }
                }
            }
        } else {
            resultObject = ObjectDataDocument.of(resultObjectData);
        }
        log.info("RebateIncomeDetailInvalid,resultObject:{}", JsonUtil.toJson(resultObject));
        if (Objects.nonNull(resultObject)) {
            ObjectData data = new ObjectData(resultObject);
            Date start = data.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
            Date end = data.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
            String lifeStatus = data.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            if (ObjectDataUtil.isCurrentTimeActive(start, end) && StringUtils.isNotEmpty(lifeStatus)) {
                rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), data, oldLifeStatus, lifeStatus);
            }
        }
        return result;
    }

    @Override
    protected void bulkUpdateObjectDataListInApproval(List<IObjectData> objectDataList, List<String> fieldsProjection) {
        if (!fieldsProjection.isEmpty()) {
            List<IObjectData> tempObjectDataList = this.serviceFacade.parallelBulkUpdateObjectData(this.actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
            resultObjectData = tempObjectDataList.stream().filter(tempObjectData -> tempObjectData.getId().equals(arg.getObjectDataId())).findAny().get();
            log.info("tempObjectDataList={},resultObjectData={}", tempObjectDataList, resultObjectData);
        }
    }

}

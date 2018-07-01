package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailBulkInvalidAction extends StandardBulkInvalidAction {
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    private CustomerAccountManager customerAccountManager;
    private Map<String, String> dataIdOldLifeStatusMap = Maps.newHashMap();
    private Map<String, String> dataIdLifeStatusMap = new HashMap<>();
    private List<IObjectData> resultDataList = Lists.newArrayList();

    @Override
    protected void validateObjectStatus() {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.validateObjectStatus();
        }
    }

    @Override
    protected void before(Arg arg) {
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        rebateOutcomeDetailManager = SpringUtil.getContext().getBean(RebateOutcomeDetailManager.class);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        super.before(arg);
        List<IObjectData> rebateIncomeObjectDatas = objectDataList.stream().filter(o -> objectDescribe.getApiName().equals(o.getDescribeApiName())).collect(Collectors.toList());
        List<String> customerIds = rebateIncomeObjectDatas.stream().map(objectData -> ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.Customer.apiName)).collect(Collectors.toList());
        List<IObjectData> customerAccountObjectDatas = customerAccountManager.listCustomerAccountIncludeInvalidByCustomerIds(actionContext.getUser(), customerIds);
        customerAccountObjectDatas.forEach(customerAccountObjectData -> {
            String lifeStatus = customerAccountObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus) || customerAccountObjectData.isDeleted()) {
                throw new ValidateException("客户和客户账户已作废，该回款已封存，无法作废！");
            }
        });
        channelValidate(rebateIncomeObjectDatas);
        Map<String, BigDecimal> customerIdAmountMap = Maps.newHashMap();
        for (IObjectData objectData : rebateIncomeObjectDatas) {
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                throw new ValidateException(String.format("返利收入主对象Id[%s]审核中不可作废", objectData.getId()));
            }
            boolean hasRebateOutcome = rebateOutcomeDetailManager.hasRebateOutcomeDetails(actionContext.getUser(), objectData.getId());
            if (hasRebateOutcome) {
                throw new ValidateException("已生成支出明细,不可作废");
            }
            if (!SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                BigDecimal amount = objectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                String customerId = ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.Customer.apiName);
                if (customerIdAmountMap.containsKey(customerId)) {
                    BigDecimal tempAmount = customerIdAmountMap.get(customerId);
                    customerIdAmountMap.put(customerId, tempAmount.add(amount));
                } else {
                    customerIdAmountMap.put(customerId, amount);
                }
            }
        }
        Map<String, IObjectData> customerIdAvailableRebateMap = customerAccountObjectDatas.stream().collect(Collectors.toMap(ob -> ObjectDataUtil.getReferenceId(ob, CustomerAccountConstants.Field.Customer.apiName), o -> o));
        customerIdAmountMap.forEach((customerId, amount) -> {
            IObjectData objectData = customerIdAvailableRebateMap.get(customerId);
            BigDecimal availableRebate = objectData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
            if (amount.compareTo(availableRebate) > 0) {
                throw new ValidateException(String.format("客户账户[%s]返利可用余额不足,禁止作废收入", objectData.getName()));
            }
        });
        if (actionContext.getAttributes() != null) {
            dataIdLifeStatusMap = actionContext.getAttribute("dataIdLifeStatusMap");
            if (dataIdLifeStatusMap == null) {
                dataIdLifeStatusMap = new HashMap<>();
            }
        }

        for (IObjectData data : objectDataList) {
            dataIdOldLifeStatusMap.put(data.getId(), data.get(SystemConstants.Field.LifeStatus.apiName, String.class));
        }

    }

    private void channelValidate(List<IObjectData> rebateIncomeObjectDatas) {
        //对于页面过来的请求，不能作废由退款创建的返利收入明细<br>
        if (!RequestUtil.isFromInner(actionContext)) {
            rebateIncomeObjectDatas.stream().forEach(iObjectData -> {
                String refundId = iObjectData.get(RebateIncomeDetailConstants.Field.Refund.apiName, String.class);
                if (StringUtils.isNotBlank(refundId)) {
                    log.warn("不能手动作废由退款创建的返利收入明细,for rebateincome id:{}, refundId:{}.", iObjectData.getId(), refundId);
                    throw new ValidateException("不能手动作废由退款创建的返利收入明细");
                }
            });
        }
    }

    @Override
    protected Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        Map<String, ApprovalFlowStartResult> result = null;
        if (RequestUtil.isFromInner(actionContext)) {
            result = new HashMap<>();
            List<IObjectData> detailsObjectData = objectDataList.stream().filter(objectData -> !objectDescribe.getApiName().equals(objectData.getDescribeApiName())).collect(Collectors.toList());
            result = super.startApprovalFlow(detailsObjectData, approvalFlowTriggerType, updatedFieldMap);
            log.debug("RebateIncome BulkInvalid arg:{},details:{}", arg, JsonUtil.toJson(result));
            for (IObjectData data : objectDataList) {
                if (!RebateIncomeDetailConstants.API_NAME.equals(data.getDescribeApiName())) {
                    continue;
                }
                String newLifeStatus = dataIdLifeStatusMap.get(data.getId());
                if (SystemConstants.LifeStatus.InChange.value.equals(newLifeStatus)) {
                    result.put(data.getId(), ApprovalFlowStartResult.SUCCESS);
                    data.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
                    IObjectData resultData = rebateIncomeDetailManager.update(actionContext.getUser(), data);
                    data.setVersion(resultData.getVersion());
                    data.setLastModifiedTime(resultData.getLastModifiedTime());
                    data.setLastModifiedBy(resultData.getLastModifiedBy());
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(newLifeStatus)) {
                    data.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
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
    protected void bulkUpdateObjectDataListInApproval(List<IObjectData> objectDataList, List<String> fieldsProjection) {
        if (!fieldsProjection.isEmpty()) {
            resultDataList = this.serviceFacade.parallelBulkUpdateObjectData(this.actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
            log.info("RebateIncomeDetailBulkInvalid,resultDataList:{}", resultDataList);
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        List<ObjectDataDocument> objectListResult = null;
        if (RequestUtil.isFromInner(actionContext)) {
            //内部调用直接更新状态
            objectListResult = Lists.newArrayList();
            List<IObjectData> inChangeObjectDataList = Lists.newArrayList();
            for (IObjectData argObjectData : objectDataList) {
                String dataId = argObjectData.getId();
                String lifeStatus = dataIdLifeStatusMap.get(dataId);
                argObjectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                    argObjectData.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                    inChangeObjectDataList.add(argObjectData);
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    objectListResult.add(ObjectDataDocument.of(argObjectData));
                }
            }
            if (CollectionUtils.isNotEmpty(inChangeObjectDataList)) {
                List<IObjectData> list = rebateIncomeDetailManager.batchUpdate(actionContext.getUser(), inChangeObjectDataList);
                objectListResult.addAll(ObjectDataDocument.ofList(list));
            }
        } else {
            objectListResult = ObjectDataDocument.ofList(resultDataList);
        }
        if (CollectionUtils.isNotEmpty(bulkOpResult.getSuccessObjectDataList())) {
            List<ObjectDataDocument> tempList = Lists.newArrayList();
            for (ObjectDataDocument objectDataResult : objectListResult) {
                tempList.add(objectDataResult);
                for (IObjectData objectData : bulkOpResult.getSuccessObjectDataList()) {
                    String dataId = objectData.getId();
                    if (dataId.equals(objectDataResult.toObjectData().getId())) {
                        tempList.remove(tempList.size() - 1);
                        tempList.add(ObjectDataDocument.of(objectData));
                    }
                }
            }
            objectListResult = tempList;
        }
        log.info("RebateIncomeDetailBulkInvalid,objectListResult:{},bulkOpResult:{}", JsonUtil.toJson(objectListResult), JsonUtil.toJson(bulkOpResult));
        for (ObjectDataDocument dataDoc : objectListResult) {
            ObjectData data = new ObjectData(dataDoc);
            String apiName = data.getDescribeApiName();
            if (RebateIncomeDetailConstants.API_NAME.equals(apiName)) {
                Date start = data.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
                Date end = data.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
                String lifeStatus = data.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                if (ObjectDataUtil.isCurrentTimeActive(start, end) && StringUtils.isNotEmpty(lifeStatus)) {
                    rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), data, dataIdOldLifeStatusMap.get(data.getId()), lifeStatus);
                }
            }
        }
        return result;
    }
}

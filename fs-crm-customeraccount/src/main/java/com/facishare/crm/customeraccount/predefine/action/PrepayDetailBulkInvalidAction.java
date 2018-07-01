package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrepayDetailBulkInvalidAction extends StandardBulkInvalidAction {
    private PrepayDetailManager prepayDetailManager;
    private CustomerAccountManager customerAccountManager;
    private Map<String, String> dataIdLifeStatusMap = new HashMap<>();
    private Map<String, String> dataIdOldLifeStatusMap = Maps.newHashMap();
    private List<IObjectData> resultDataList = Lists.newArrayList();

    @Override
    protected void validateObjectStatus() {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.validateObjectStatus();
        }
    }

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        for (IObjectData data : objectDataList) {
            dataIdOldLifeStatusMap.put(data.getId(), data.get(SystemConstants.Field.LifeStatus.apiName, String.class));
        }
        log.debug("PrepayDetailBulk BulkInvalid actioncontext ReqeustSource:{}", actionContext.getRequestSource());
        relateObjectValidate();
        channelValidate();
        amountValidate();
        if (actionContext.getAttributes() != null) {
            dataIdLifeStatusMap = actionContext.getAttribute("dataIdLifeStatusMap");
            if (dataIdLifeStatusMap == null) {
                dataIdLifeStatusMap = new HashMap<>();
            }
        }

        log.info("objectDataList={}", JsonUtil.toJson(objectDataList));
    }

    private void relateObjectValidate() {
        if (CollectionUtils.isEmpty(objectDataList)) {
            return;
        }
        List<String> customerAccountList = objectDataList.stream().map(x -> x.get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class)).distinct().collect(Collectors.toList());

        QueryResult<IObjectData> customerAccountObjRs = prepayDetailManager.queryInvalidDataByField(actionContext.getUser(), CustomerAccountConstants.API_NAME, SystemConstants.Field.Id.apiName, Lists.newArrayList(customerAccountList), 0, 10);
        if (null != customerAccountObjRs && CollectionUtils.isNotEmpty(customerAccountObjRs.getData())) {
            for (IObjectData customerAccountObj : customerAccountObjRs.getData()) {
                String customerAccountLifeStatus = customerAccountObj.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                if (SystemConstants.LifeStatus.Invalid.value.equals(customerAccountLifeStatus)) {
                    String formattedmsg = String.format("客户和客户账户已作废，该预存款/返利记录已封存，无法作废, 客户账户名：{%s}", customerAccountObj.getName());
                    throw new ValidateException(formattedmsg);
                }
            }
        }
    }

    private void channelValidate() {
        //对于页面过来的请求，不能作废由回款/退款创建的预存款明细<br>
        if (!RequestUtil.isFromInner(actionContext)) {
            objectDataList.stream().forEach(iObjectData -> {
                String paymentId = iObjectData.get(PrepayDetailConstants.Field.OrderPayment.apiName, String.class);
                String refundId = iObjectData.get(PrepayDetailConstants.Field.Refund.apiName, String.class);
                if (StringUtils.isNotBlank(paymentId)) {
                    log.warn("不能手动作废由回款创建的预存款明细,for prepayId:{}, paymentId:{}.", iObjectData.getId(), paymentId);
                    throw new ValidateException("由回款关联创建的预存款记录，暂不支持作废！");
                }
                if (StringUtils.isNotBlank(refundId)) {
                    log.warn("不能手动作废由退款创建的预存款明细,for prepayId:{}, refundId:{}.", iObjectData.getId(), refundId);
                    throw new ValidateException("由退款关联创建的预存款记录，暂不支持作废！");
                }
            });
        }
    }

    private void amountValidate() {
        //过滤出预付款主对象，（因为底层代码里objectDataList里包含了从对象）
        List<IObjectData> prepayObjectList = objectDataList.stream().filter(o -> objectDescribeMap.containsKey(o.getDescribeApiName())).collect(Collectors.toList());
        log.debug("begin amountValidate,for prepayObjList:{}", prepayObjectList);
        Map<String, List<IObjectData>> customerIdPrepaysMap = prepayObjectList.stream().collect(Collectors.groupingBy(preObj -> ObjectDataUtil.getReferenceId(preObj, PrepayDetailConstants.Field.Customer.apiName)));
        CustomerAccountManager customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        List<String> customerIdList = new ArrayList<>(customerIdPrepaysMap.keySet());
        List<IObjectData> customerAccountList = customerAccountManager.listCustomerAccountByCustomerIds(actionContext.getUser(), customerIdList);
        log.debug("amountValidate->when bulkInvalid PrepayDetailObj,for customerIdList:{}", customerIdList);
        customerAccountList.forEach(customerAccountObj -> {
            String customerId = customerAccountObj.get(CustomerAccountConstants.Field.Customer.apiName, String.class);
            List<IObjectData> prepayObjs = customerIdPrepaysMap.get(customerId);
            prepayObjs.forEach(prepayObj -> {
                if (needValidateAmount(prepayObj)) {
                    BigDecimal availableAmount = customerAccountObj.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
                    BigDecimal amountInPrepayObj = prepayObj.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
                    if (availableAmount.compareTo(amountInPrepayObj) == -1) {
                        log.warn("客户账户预存款可用金额不够，不能作废预存款收入,for prepayId:{}", prepayObj.getId());
                        throw new ValidateException("客户账户预存款可用金额不够，不能作废预存款收入");
                    }
                }
            });
        });
    }

    private boolean needValidateAmount(IObjectData newPrepayObj) {
        String incomeType = newPrepayObj.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        String oldLifeStatus = dataIdOldLifeStatusMap.get(newPrepayObj.getId());
        // 如果oldLifeStatus是 ineffective，说明是驳回做作废操作，此种情况下不需要校验余额（因为它不会扣客户账户余额）。
        if (null == oldLifeStatus) {
            log.error("oldLifeStatus is null,for dataIdOldLifeStatusMap:{},newPrepayObj:{}", dataIdOldLifeStatusMap, newPrepayObj);
            throw new RuntimeException("error occur when getting oldLifeStatus ");
        }
        if (SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
            log.info("prepay lifestatus is ineffective(may be rejected),so no need to validate amount when do invalid,for prepayObj:{}", newPrepayObj);
            return false;
        }
        if (StringUtils.isEmpty(incomeType)) {
            log.debug("this is outcome prepayObj,no need to invalid amount when do invalid,for prepayId:{{}", newPrepayObj);
            return false;
        }
        return true;
    }

    @Override
    protected Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        Map<String, ApprovalFlowStartResult> result = null;
        if (RequestUtil.isFromInner(actionContext)) {
            List<IObjectData> detailsObjectData = objectDataList.stream().filter(objectData -> !objectDescribe.getApiName().equals(objectData.getDescribeApiName())).collect(Collectors.toList());
            //触发从对象流程
            result = super.startApprovalFlow(detailsObjectData, ApprovalFlowTriggerType.INVALID, Maps.newHashMap());
            log.info("Prepay BulkInvalid arg:{},details:{}", arg, JsonUtil.toJson(result));
            for (IObjectData data : objectDataList) {
                if (!PrepayDetailConstants.API_NAME.equals(data.getDescribeApiName())) {
                    continue;
                }
                String newLifeStatus = dataIdLifeStatusMap.get(data.getId());
                if (SystemConstants.LifeStatus.InChange.value.equals(newLifeStatus)) {
                    result.put(data.getId(), ApprovalFlowStartResult.SUCCESS);
                    data.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
                    IObjectData resultData = prepayDetailManager.update(actionContext.getUser(), data);
                    data.setVersion(resultData.getVersion());
                    data.setLastModifiedTime(resultData.getLastModifiedTime());
                    data.setLastModifiedBy(resultData.getLastModifiedBy());
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(newLifeStatus)) {
                    data.set(SystemConstants.Field.LifeStatus.apiName, newLifeStatus);
                    prepayDetailManager.update(actionContext.getUser(), data);
                    result.put(data.getId(), ApprovalFlowStartResult.APPROVAL_NOT_EXIST);
                }
            }
        } else {
            result = super.startApprovalFlow(objectDataList, approvalFlowTriggerType, updatedFieldMap);
        }
        log.info("startApprovalFlow result={}", result);
        return result;
    }

    @Override
    protected void bulkUpdateObjectDataListInApproval(List<IObjectData> objectDataList, List<String> fieldsProjection) {
        if (!fieldsProjection.isEmpty()) {
            resultDataList = this.serviceFacade.parallelBulkUpdateObjectData(this.actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
            log.info("PrepayDetailBulkInvalidAction,resultDataList:{}", resultDataList);
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
                List<IObjectData> list = prepayDetailManager.batchUpdate(actionContext.getUser(), inChangeObjectDataList);
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
                        //替换数据目的在于获取最新version字段,防止更新失败
                        tempList.remove(tempList.size() - 1);
                        tempList.add(ObjectDataDocument.of(objectData));
                    }
                }
            }
            objectListResult = tempList;
        }
        log.info("PrepayDetailBulkInvalidAction,objectListResult:{},bulkOpResult:{}", JsonUtil.toJson(objectListResult), JsonUtil.toJson(bulkOpResult));
        for (ObjectDataDocument dataDoc : objectListResult) {
            ObjectData data = new ObjectData(dataDoc);
            String apiName = data.getDescribeApiName();
            if (PrepayDetailConstants.API_NAME.equals(apiName)) {
                prepayDetailManager.updateBalance(actionContext.getUser(), data, dataIdOldLifeStatusMap.get(data.getId()));
            }
        }
        return result;
    }
}

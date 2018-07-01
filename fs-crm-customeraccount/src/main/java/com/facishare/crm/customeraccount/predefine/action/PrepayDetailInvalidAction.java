package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrepayDetailInvalidAction extends StandardInvalidAction {
    private PrepayDetailManager prepayDetailManager;
    private String lifeStatus = null;
    private String oldLifeStatus = null;
    private IObjectData resultObjectData;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        IObjectData tempObjectData = objectDataList.stream().filter(objectData -> objectData.getId().equals(arg.getObjectDataId())).findFirst().get();
        String customerAccountId = tempObjectData.get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class);

        relateObjectValidate();
        channelValidate(tempObjectData);
        oldLifeStatus = tempObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (RequestUtil.isFromInner(actionContext)) {
            lifeStatus = actionContext.getAttribute("lifeStatus");
        }
        amountValidate();
    }

    private boolean needValidateAmount(IObjectData newPrepayObj) {
        String incomeType = newPrepayObj.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        // 如果oldLifeStatus是 ineffective，说明是驳回后再做作废操作，此种情况下不需要校验余额（因为它不会扣客户账户余额）。

        if (null == oldLifeStatus) {
            log.error("oldLifeStatus is null,for oldLifeStatus:{},newPrepayObj:{}", oldLifeStatus, newPrepayObj);
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

    private void relateObjectValidate() {
        if (CollectionUtils.isEmpty(objectDataList)) {
            return;
        }
        String customerAccountId = objectDataList.get(0).get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class);
        String prepayId = objectDataList.get(0).getId();
        QueryResult<IObjectData> customerAccountObjRs = prepayDetailManager.queryInvalidDataByField(actionContext.getUser(), CustomerAccountConstants.API_NAME, SystemConstants.Field.Id.apiName, Lists.newArrayList(customerAccountId), 0, 10);
        if (null != customerAccountObjRs && CollectionUtils.isNotEmpty(customerAccountObjRs.getData())) {
            IObjectData customerAccountObj = customerAccountObjRs.getData().get(0);
            String customerAccountLifeStatus = customerAccountObj.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            log.warn("客户和客户账户已作废，该预存款/返利记录已封存，无法作废,for prepayId:{}, customeraccountId:{},customerAccountLifeStatus:{}", prepayId, customerAccountId, customerAccountLifeStatus);
            if (SystemConstants.LifeStatus.Invalid.value.equals(customerAccountLifeStatus)) {
                throw new ValidateException("客户和客户账户已作废，该预存款/返利记录已封存，无法作废！");
            }

        }
    }

    private void amountValidate() {
        //过滤出预付款主对象，（因为底层代码里objectDataList里包含了从对象）
        List<IObjectData> prepayObjectList = objectDataList.stream().filter(o -> objectDescribeMap.containsKey(o.getDescribeApiName())).collect(Collectors.toList());
        log.debug("begin amountValidate,for prepayObjList:{}", prepayObjectList);
        //把list变成map便于后面的获取
        Map<String, IObjectData> prepayCustomerIdMaps = prepayObjectList.stream().collect(Collectors.toMap(preObj -> ObjectDataUtil.getReferenceId(preObj, PrepayDetailConstants.Field.Customer.apiName), obj -> obj));

        CustomerAccountManager customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        List<String> customerIdList = new ArrayList<>(prepayCustomerIdMaps.keySet());
        List<IObjectData> customerAccountList = customerAccountManager.listCustomerAccountByCustomerIds(actionContext.getUser(), customerIdList);
        log.debug("amountValidate->when bulkInvalid PrepayDetailObj,for customerId:{}", customerIdList);
        customerAccountList.stream().forEach(customerAccountObj -> {
            String customerId = customerAccountObj.get(CustomerAccountConstants.Field.Customer.apiName, String.class);
            IObjectData prepayObj = prepayCustomerIdMaps.get(customerId);

            if (needValidateAmount(prepayObj)) {
                BigDecimal availableAmount = customerAccountObj.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
                BigDecimal amountInPrepayObj = prepayObj.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
                if (availableAmount.compareTo(amountInPrepayObj) == -1) {
                    log.warn("客户账户预付款可用金额不够，不能作废预付款收入,for prepayId:{}", prepayObj.getId());
                    throw new ValidateException("客户账户预付款可用金额不够，不能作废预付款收入");
                }
            }
        });
    }

    private void channelValidate(IObjectData objectData) {
        //对于页面过来的请求，不能作废由回款/退款创建的预存款明细<br>
        if (!RequestUtil.isFromInner(actionContext)) {
            String paymentId = objectData.get(PrepayDetailConstants.Field.OrderPayment.apiName, String.class);
            String refundId = objectData.get(PrepayDetailConstants.Field.Refund.apiName, String.class);
            if (StringUtils.isNotBlank(paymentId)) {
                log.warn("不能手动作废由回款创建的预存款明细,for prepayId:{}, paymentId:{}.", objectData.getId(), paymentId);
                throw new ValidateException("由回款关联创建的预存款记录，暂不支持作废！");
            }
            if (StringUtils.isNotBlank(refundId)) {
                log.warn("不能手动作废由退款创建的预存款明细,for prepayId:{}, refundId:{}.", objectData.getId(), refundId);
                throw new ValidateException("由退款关联创建的预存款记录，暂不支持作废！");
            }
        }
    }

    @Override
    protected Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList, ApprovalFlowTriggerType approvalFlowTriggerType, Map<String, Map<String, Object>> updatedFieldMap) {
        Map<String, ApprovalFlowStartResult> result = null;
        if (RequestUtil.isFromInner(actionContext)) {
            result = new HashMap<>();
            List<IObjectData> detailsObjectData = objectDataList.stream().filter(objectData -> !objectDescribe.getApiName().equals(objectData.getDescribeApiName())).collect(Collectors.toList());
            result = super.startApprovalFlow(detailsObjectData, approvalFlowTriggerType, updatedFieldMap);
            log.debug("arg:{},details:{}", arg, JsonUtil.toJson(result));
            for (IObjectData data : objectDataList) {
                if (!arg.getObjectDataId().equals(data.getId())) {
                    continue;
                }
                if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                    result.put(data.getId(), ApprovalFlowStartResult.SUCCESS);
                    data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                    IObjectData resultData = prepayDetailManager.update(actionContext.getUser(), data);
                    data.setVersion(resultData.getVersion());
                    data.setLastModifiedTime(resultData.getLastModifiedTime());
                    data.setLastModifiedBy(resultData.getLastModifiedBy());
                } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                    prepayDetailManager.update(actionContext.getUser(), data);
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
            List<IObjectData> tempObjectDataList = this.serviceFacade.parallelBulkUpdateObjectData(this.actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
            resultObjectData = tempObjectDataList.stream().filter(tempObjectData -> tempObjectData.getId().equals(arg.getObjectDataId())).findAny().get();
            log.info("tempObjectDataList={},resultObjectData={}", tempObjectDataList, resultObjectData);
        }
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
                List<IObjectData> list = prepayDetailManager.batchUpdate(actionContext.getUser(), inChangeObjectDataList);
                for (IObjectData data : list) {
                    if (data.getId().equals(arg.getObjectDataId())) {
                        resultObject = ObjectDataDocument.of(data);
                    }
                }
            }
        } else {
            resultObject = ObjectDataDocument.of(resultObjectData);
        }
        log.info("PrepayDetailInvalidAction,resultObject:{}", JsonUtil.toJson(resultObject));
        if (Objects.nonNull(resultObject)) {
            ObjectData data = new ObjectData(resultObject);
            prepayDetailManager.updateBalance(actionContext.getUser(), data, oldLifeStatus);
        }
        return result;
    }
}

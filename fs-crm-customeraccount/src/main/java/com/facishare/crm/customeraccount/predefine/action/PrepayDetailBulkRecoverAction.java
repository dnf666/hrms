package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.CustomerAccountRecordLogger;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrepayDetailBulkRecoverAction extends StandardBulkRecoverAction {
    private CustomerAccountManager customerAccountManager;
    private PrepayDetailManager prepayDetailManager;
    private CrmManager crmManager;

    @Override
    public void before(Arg arg) {
        super.before(arg);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        String objectApiName = arg.getObjectDescribeAPIName();
        List<String> ids = arg.getIdList();
        QueryResult<IObjectData> queryResult = prepayDetailManager.queryInvalidDataByField(actionContext.getUser(), objectApiName, SystemConstants.Field.Id.apiName, ids, 0, ids.size());
        List<IObjectData> prepayDatas = queryResult.getData();
        log.info("begin recover,for prepayDatas:{}", prepayDatas);
        if (!RequestUtil.isFromInner(actionContext)) {
            for (IObjectData objectData : prepayDatas) {
                String orderPaymentId = ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.OrderPayment.apiName);
                String refundId = ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Refund.apiName);

                if (org.apache.commons.lang.StringUtils.isNotBlank(orderPaymentId)) {
                    List<IObjectData> paymentList = crmManager.listInvalidOrderPaymentByIds(actionContext.getUser(), Lists.newArrayList(orderPaymentId));
                    if (CollectionUtils.isEmpty(paymentList)) {
                        throw new ValidateException("error occur when query order payment by orderPaymentId,for orderPaymentId=" + orderPaymentId);
                    }
                    throw new ValidateException(String.format("预存款{%s}有关联回款，请恢复关联对象，回款编号{%s}", objectData.getName(), paymentList.get(0).getName()));
                }

                if (org.apache.commons.lang.StringUtils.isNotBlank(refundId)) {
                    List<IObjectData> refundList = crmManager.listInvalidRefundByIds(actionContext.getUser(), Lists.newArrayList(refundId));
                    if (CollectionUtils.isEmpty(refundList)) {
                        throw new ValidateException("error occur when query refund by refundId,for refundId=" + refundId);
                    }
                    throw new ValidateException(String.format("预存款{%s}有关联退款，请恢复关联对象，退款编号:{%s}", objectData.getName(), refundList.get(0).getName()));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            Map<String, List<IObjectData>> customerIdPrepayDatasMap = prepayDatas.stream().filter(x -> {
                String outcomeType = x.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
                return StringUtils.isNotEmpty(outcomeType);
            }).collect(Collectors.groupingBy(x -> ObjectDataUtil.getReferenceId(x, PrepayDetailConstants.Field.Customer.apiName)));
            Set<String> customerIds = customerIdPrepayDatasMap.keySet();
            if (CollectionUtils.isEmpty(customerIds)) {
                return;
            }
            List<IObjectData> customerAccountDatas = customerAccountManager.listCustomerAccountIncludeInvalidByCustomerIds(actionContext.getUser(), Lists.newArrayList(customerIds));
            Map<String, IObjectData> customerAccountMap = customerAccountDatas.stream().collect(Collectors.toMap(x -> ObjectDataUtil.getReferenceId(x, CustomerAccountConstants.Field.Customer.apiName), y -> y));
            customerIdPrepayDatasMap.forEach((customerId, prepayList) -> {
                IObjectData customerAccountData = customerAccountMap.get(customerId);
                BigDecimal prepayAvailable = ObjectDataUtil.getBigDecimal(customerAccountData, CustomerAccountConstants.Field.PrepayAvailableBalance.apiName);
                prepayList.forEach(prepay -> {
                    BigDecimal prepayAmount = ObjectDataUtil.getBigDecimal(prepay, PrepayDetailConstants.Field.Amount.apiName);
                    String prepayName = prepay.getName();
                    if (prepayAvailable.compareTo(prepayAmount) < 0) {
                        throw new ValidateException(String.format("{%s}所属客户账户预存款可用余额不足，不可恢复", prepayName));
                    }
                });
            });
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        super.after(arg, result);
        String objectApiName = arg.getObjectDescribeAPIName();
        List<String> ids = arg.getIdList();
        List<IObjectData> prepayDatas = serviceFacade.findObjectDataByIds(actionContext.getTenantId(), ids, objectApiName);
        List<IObjectData> prepayResultDatas = prepayDatas.stream().filter(objectData -> {
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            return SystemConstants.LifeStatus.Normal.value.equals(lifeStatus);
        }).collect(Collectors.toList());
        prepayResultDatas.stream().forEach(objectData -> {
            BigDecimal prepayAmount = objectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);
            String customerId = ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Customer.apiName);
            String incomeType = objectData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
            String outcomeType = objectData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
            String info = CustomerAccountRecordLogger.generatePrepayInfo(objectData.getId(), SystemConstants.LifeStatus.Invalid.value, objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class));

            if (StringUtils.isNotEmpty(incomeType)) {
                customerAccountManager.updatePrepayBalance(actionContext.getUser(), customerId, prepayAmount, BigDecimal.valueOf(0), info, objectData.getId());
            } else if (StringUtils.isNotEmpty(outcomeType)) {
                customerAccountManager.updatePrepayBalance(actionContext.getUser(), customerId, BigDecimal.valueOf(0).subtract(prepayAmount), BigDecimal.valueOf(0), info, objectData.getId());
            }
        });
        return result;
    }
}

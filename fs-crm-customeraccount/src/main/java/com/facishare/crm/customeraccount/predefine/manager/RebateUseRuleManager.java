package com.facishare.crm.customeraccount.predefine.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.HeaderUtil;
import com.facishare.crm.customeraccount.util.HttpUtil;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.util.RangeVerify;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.jaxrs.model.InnerAPIResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RebateUseRuleManager extends CommonManager {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CrmRestApi crmRestApi;

    public Map<String, RebateUseRuleValidateModel.RebateUseRuleValidateResult> validate(User user, String customerId, Map<String, BigDecimal> orderIdRebateAmountMap) {
        Optional<IObjectData> rebateUseRuleObjectDataOptional = getRebateUseRuleByCustomerId(user, customerId);
        log.info("rebateUseRuleObjectData:{}", rebateUseRuleObjectDataOptional);
        List<String> orderIds = Lists.newArrayList(orderIdRebateAmountMap.keySet());
        Map<String, BigDecimal> orderIdUsedRebateAmountMap = getUsedRebateAmountByOrderIds(user, orderIds);
        log.info("orderIdUsedRebateAmountMap:{}", orderIdUsedRebateAmountMap);
        Map<String, BigDecimal> orderIdTradeMoneyMap = getSalesOrderTradeMoneyByOrderIds(user, orderIds);
        log.info("orderIdTradeMoneyMap:{}", orderIdTradeMoneyMap);
        if (rebateUseRuleObjectDataOptional.isPresent()) {
            IObjectData rebateUseRuleObjectData = rebateUseRuleObjectDataOptional.get();
            BigDecimal minOrderAmount = ObjectDataUtil.getBigDecimal(rebateUseRuleObjectData, RebateUseRuleConstants.Field.MinOrderAmount.apiName);
            BigDecimal usedMaxAmount = ObjectDataUtil.getBigDecimal(rebateUseRuleObjectData, RebateUseRuleConstants.Field.UsedMaxAmount.apiName);
            BigDecimal usedMaxPrecent = ObjectDataUtil.getBigDecimal(rebateUseRuleObjectData, RebateUseRuleConstants.Field.UsedMaxPrecent.apiName);
            Map<String, RebateUseRuleValidateModel.RebateUseRuleValidateResult> result = Maps.newHashMap();
            orderIds.forEach(orderId -> {
                RebateUseRuleValidateModel.RebateUseRuleValidateResult validateResult = new RebateUseRuleValidateModel.RebateUseRuleValidateResult();
                BigDecimal usedRebateAmount = orderIdUsedRebateAmountMap.getOrDefault(orderId, BigDecimal.ZERO);
                BigDecimal rebateAmountToUse = orderIdRebateAmountMap.get(orderId);
                BigDecimal tradeMoney = orderIdTradeMoneyMap.get(orderId);
                BigDecimal maxRebateAmountCanUse = BigDecimal.ZERO;
                if (usedMaxAmount.compareTo(BigDecimal.ZERO) > 0 || usedMaxPrecent.compareTo(BigDecimal.ZERO) > 0) {
                    if (usedMaxAmount.compareTo(BigDecimal.ZERO) > 0 && usedMaxPrecent.compareTo(BigDecimal.ZERO) <= 0) {
                        maxRebateAmountCanUse = usedMaxAmount;
                    } else if (usedMaxAmount.compareTo(BigDecimal.ZERO) <= 0 && usedMaxPrecent.compareTo(BigDecimal.ZERO) > 0) {
                        maxRebateAmountCanUse = usedMaxPrecent.multiply(tradeMoney).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
                    } else {
                        BigDecimal temp = usedMaxPrecent.multiply(tradeMoney).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
                        maxRebateAmountCanUse = temp.compareTo(usedMaxAmount) <= 0 ? temp : usedMaxAmount;
                    }
                }
                //                maxRebateAmountCanUse = temp.compareTo(BigDecimal.ZERO) <= 0 ? usedMaxAmount : (usedMaxAmount.compareTo(temp) > 0 ? temp : usedMaxAmount);
                BigDecimal leftRebateCanUse = maxRebateAmountCanUse.subtract(usedRebateAmount);
                if (minOrderAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    if (rebateAmountToUse.compareTo(leftRebateCanUse) > 0) {
                        validateResult.setCanUseRebate(false);
                        validateResult.setMaxRebateAmountToUse(leftRebateCanUse);
                    } else {
                        validateResult.setCanUseRebate(true);
                        validateResult.setMaxRebateAmountToUse(leftRebateCanUse);
                    }
                } else if (tradeMoney.compareTo(minOrderAmount) < 0 || rebateAmountToUse.compareTo(leftRebateCanUse) > 0) {
                    validateResult.setCanUseRebate(false);
                    validateResult.setMaxRebateAmountToUse(tradeMoney.compareTo(minOrderAmount) < 0 ? BigDecimal.ZERO : leftRebateCanUse);
                } else {
                    validateResult.setCanUseRebate(true);
                    validateResult.setMaxRebateAmountToUse(leftRebateCanUse);
                }
                result.put(orderId, validateResult);
            });
            return result;
        } else {
            return orderIds.stream().collect(Collectors.toMap(x -> x, v -> {
                RebateUseRuleValidateModel.RebateUseRuleValidateResult rebateUseRuleValidateResult = new RebateUseRuleValidateModel.RebateUseRuleValidateResult();
                //没有使用规则限制
                rebateUseRuleValidateResult.setCanUseRebate(Boolean.TRUE);
                rebateUseRuleValidateResult.setHasRebateUseRule(Boolean.FALSE);
                return rebateUseRuleValidateResult;
            }));
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, BigDecimal> getUsedRebateAmountByOrderIds(User user, List<String> orderIds) {
        String url = ConfigCenter.frameworkUrl + "/customer_payment/service/get_order_rebate_money";
        try {
            InnerAPIResult innerAPIResult = HttpUtil.post(url, HeaderUtil.getCrmHeader(user.getTenantId(), user.getUserId()), orderIds, TypeUtils.parameterize(InnerAPIResult.class));
            if (innerAPIResult.getErrCode() != 0) {
                log.warn("getUsedRebateAmountByOrderIds fail,result:{}", innerAPIResult);
                throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_PAYMENT_ERROR, innerAPIResult.getErrMessage());
            }
            Map<String, Double> resultMap = (Map<String, Double>) innerAPIResult.getResult();
            Map<String, BigDecimal> result = Maps.newHashMap();
            resultMap.forEach((key, value) -> result.put(key, BigDecimal.valueOf(value)));
            return result;
        } catch (IOException e) {
            log.warn("getUsedRebateAmountByOrderIds error,user:{},orderId:{}", user, orderIds, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_PAYMENT_ERROR, e.getMessage());
        }
    }

    private Map<String, BigDecimal> getSalesOrderTradeMoneyByOrderIds(User user, List<String> orderIds) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-fs-ei", user.getTenantId());
        headers.put("x-fs-userInfo", user.getUserId());
        String[] orderIdArray = new String[orderIds.size()];
        orderIdArray = orderIds.toArray(orderIdArray);
        SalesOrderModel.GetByIdsResult result = crmRestApi.getCustomerOrderByIds(orderIdArray, headers);
        if (!result.isSuccess()) {
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.QUERY_CUSTOMER_ERROR, result.getMessage());
        }
        return result.getValue().stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderVo::getCustomerTradeId, SalesOrderModel.SalesOrderVo::getTradeMoney));
    }

    public Optional<IObjectData> getRebateUseRuleByCustomerId(User user, String customerId) {
        String tenantId = user.getTenantId();
        List<IFilter> filters = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(year, month, day, 0, 0, 0);
        startCalendar.clear(Calendar.MILLISECOND);
        SearchUtil.fillFilterLTE(filters, RebateUseRuleConstants.Field.StartTime.apiName, startCalendar.getTimeInMillis());
        SearchUtil.fillFilterGTE(filters, RebateUseRuleConstants.Field.EndTime.apiName, startCalendar.getTimeInMillis());
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.TennantID.apiName, tenantId);
        SearchUtil.fillFilterEq(filters, RebateUseRuleConstants.Field.Status.apiName, true);
        List<OrderBy> orders = Lists.newArrayList();
        SearchUtil.fillOrderBy(orders, RebateUseRuleConstants.Field.StartTime.apiName, false);
        SearchUtil.fillOrderBy(orders, RebateUseRuleConstants.Field.EndTime.apiName, true);
        SearchUtil.fillOrderBy(orders, SystemConstants.Field.CreateTime.apiName, false);
        QueryResult<IObjectData> result = searchQuery(user, RebateUseRuleConstants.API_NAME, filters, orders, 0, 1000);
        //过滤客户适用范围
        IObjectDescribe customerDescribe = serviceFacade.findObject(tenantId, ObjectAPINameMapping.Account.getApiName());
        IObjectData customerObjectData = serviceFacade.findObjectData(tenantId, customerId, customerDescribe);
        if (CollectionUtils.notEmpty(result.getData())) {
            List<IObjectData> rebateUseRulesInRange = Lists.newArrayList();
            for (IObjectData objectData : result.getData()) {
                if (isRebateUseRuleInCustomerRange(customerDescribe, customerObjectData, objectData)) {
                    rebateUseRulesInRange.add(objectData);
                }
            }
            if (rebateUseRulesInRange.size() >= 1) {
                //获取唯一使用规则
                return Optional.of(rebateUseRulesInRange.get(0));
            }
        }
        return Optional.empty();
    }

    private boolean isRebateUseRuleInCustomerRange(IObjectDescribe customerDescribe, IObjectData customerObjectData, IObjectData rebateUseRuleData) {
        // 根据当前客户的适用范围过滤符合条件的促销表
        Object customerRange = rebateUseRuleData.get(RebateUseRuleConstants.Field.CustomerRange.apiName);
        if (Objects.nonNull(customerRange) && StringUtils.isNotBlank(customerRange.toString())) {
            JSONObject accountRangeObj = JSON.parseObject(customerRange.toString());
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                return RangeVerify.verifyConditions(customerDescribe, customerObjectData, accountRangeObj.getJSONObject("value"));
            }
        }
        return true;
    }

}

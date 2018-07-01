package com.facishare.crm.customeraccount.predefine.remote;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.GetUsedCreditAmount;
import com.facishare.crm.rest.dto.QueryCustomersByPage;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.ActionContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrmManager {
    @Autowired
    private CrmRestApi crmRequestApi;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ObjectDataServiceImpl objectDataService;

    public boolean syncCustomerAccountSwitchToCrm(User user) {
        //设置开关，同步客户账户开关到CRM
        //约定了key=29,value=1 表示 客户账户开关已经启用了
        SyncTenantSwitchModel.Arg syncCustomerAccountArg = new SyncTenantSwitchModel.Arg();
        syncCustomerAccountArg.setKey("29");
        syncCustomerAccountArg.setValue("1");
        SyncTenantSwitchModel.Result syncCustomerAccountSwitchResult = crmRequestApi.syncTenantSwitch(syncCustomerAccountArg, this.getHeaders(user.getTenantId(), user.getUserId()));
        log.info("Sync Switch to SFA ,syncResult:{}", syncCustomerAccountSwitchResult);
        return syncCustomerAccountSwitchResult.getSuccess();
    }

    public QueryCustomersByPage.Result queryCustomers(User user, int offset, int limit) {
        QueryCustomersByPage.Arg arg = new QueryCustomersByPage.Arg();
        arg.setOffset(offset);
        arg.setLimit(limit);
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        QueryCustomersByPage.Result queryCustomersByPageResult = crmRequestApi.queryCustomersByPage(arg, headers);
        return queryCustomersByPageResult;
    }

    public List<String> listTenantIdsOfLackCustomerAccountDatas(List<String> tenantIds) {
        List<String> resultTenantIds = Lists.newArrayList();
        if (CollectionUtils.isEmpty(tenantIds)) {
            return resultTenantIds;
        }
        String countCustomerSqlFormat = "select count(1) from customer where is_deleted='0' and ei =%d";
        String countCustomerAccountSqlFormat = "select count(1) from customer_account where is_deleted <> -1 and tenant_id = %s";
        try {
            for (String tenantId : tenantIds) {
                QueryResult<IObjectData> resultCustomerCount = objectDataService.findBySql(String.format(countCustomerSqlFormat, Integer.valueOf(tenantId)), tenantId, SystemConstants.AccountApiName);
                int customerCount = resultCustomerCount.getData().get(0).get("count", Integer.class);
                QueryResult<IObjectData> resultCustomerAccountCount = objectDataService.findBySql(String.format(countCustomerAccountSqlFormat, "\'" + tenantId + "\'"), tenantId, SystemConstants.AccountApiName);
                int customerAccountCount = resultCustomerAccountCount.getData().get(0).get("count", Integer.class);
                if (customerAccountCount != customerCount) {
                    if (customerCount > customerAccountCount) {
                        resultTenantIds.add(tenantId);
                    } else {
                        log.warn(String.format("企业{%s}客户数{%d}小于客户账户数{%d}", tenantId, customerCount, customerAccountCount));
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.warn("getTenantIdsOfLackCustomerAccountDatas,tenantIds:{}", tenantIds, e);
        }
        return resultTenantIds;
    }

    public List<IObjectData> listPlainCustomersFromPg(User user, List<String> customerIds, int offset, int limit) {
        String sqlFormat = "select customer_id,status from customer where is_deleted='0' and ei=%d %s limit %d offset %d";
        String sql;
        if (CollectionUtils.isEmpty(customerIds)) {
            sql = String.format(sqlFormat, Integer.valueOf(user.getTenantId()), "", limit, offset);
        } else {
            sql = String.format(sqlFormat, Integer.valueOf(user.getTenantId()), String.format("and customer_id in(%s)", Joiner.on(",").join(customerIds.stream().map(id -> "\'" + id + "\'").collect(Collectors.toList()))), customerIds.size(), offset);
        }
        try {
            QueryResult<IObjectData> queryResult = objectDataService.findBySql(sql, user.getTenantId(), SystemConstants.AccountApiName);
            return queryResult.getData();
        } catch (MetadataServiceException e) {
            log.warn("listPlainCustomersFromPg error,user:{},offset:{},limit:{}", user, offset, limit, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.QUERY_CUSTOMER_FROM_PG_ERROR, "查询客户异常");
        }
    }

    public Map<String, Integer> listCustomerStatusBeforeInvalid(User user, List<String> customerIds) {
        Map<String, Integer> customerOldStatusMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(customerIds)) {
            return customerOldStatusMap;
        }
        String sqlFormat = "select data_id,old_status from invalid_object_record where ei=%d and object_type=2 and data_id in (%s)";
        String sql = String.format(sqlFormat, Integer.parseInt(user.getTenantId()), Joiner.on(",").join(customerIds.stream().map(id -> "\'" + id + "\'").collect(Collectors.toList())));
        try {
            QueryResult<IObjectData> queryResult = objectDataService.findBySql(sql, user.getTenantId(), SystemConstants.AccountApiName);
            List<IObjectData> invalidObjectRecords = queryResult.getData();
            if (CollectionUtils.isEmpty(invalidObjectRecords)) {
                customerIds.forEach(customerId -> {
                    customerOldStatusMap.put(customerId, 2);
                });
            } else {
                invalidObjectRecords.forEach(objectData -> {
                    String dataId = objectData.get("data_id", String.class);
                    Integer oldStatus = objectData.get("old_status", Integer.class);
                    customerOldStatusMap.put(dataId, oldStatus);
                });
                customerIds.stream().filter(customerId -> !customerOldStatusMap.containsKey(customerId)).forEach(customerId -> {
                    customerOldStatusMap.put(customerId, 2);
                });
            }
            return customerOldStatusMap;
        } catch (MetadataServiceException e) {
            log.warn("listPlainCustomersFromPg error,user:{},customerIds:{}", user, customerIds, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.QUERY_CUSTOMER_FROM_PG_ERROR, "查询客户作废前状态异常");
        }
    }

    public int getAllCustomerCount(User user) {
        //调用该分页接口只是为了从totalCount获取总的客户数目<br>
        QueryCustomersByPage.Result queryCustomersByPageResult = this.queryCustomers(user, 0, 1);
        log.debug("QueryCustomersByPage.Result:{}", queryCustomersByPageResult);
        if (!queryCustomersByPageResult.isSuccess()) {
            log.info("查询客户总数报错，for user:{}", user);
            throw new RuntimeException("查询客户账户综总数报错");
        }
        Integer totalCount = queryCustomersByPageResult.getValue().getPage().getTotalCount();
        return totalCount;
    }

    public BigDecimal getUsedCreditAmount(User user, String customerId) {
        GetUsedCreditAmount.Arg arg = new GetUsedCreditAmount.Arg();
        arg.setCustomerID(customerId);
        GetUsedCreditAmount.Result result = crmRequestApi.getUsedCreditAmount(arg, this.getHeaders(user.getTenantId(), user.getUserId()));
        if (!result.isSuccess()) {
            log.error("error occur  when getUsedCreditAmount,for customerId:{},tenantId:{},result={}", customerId, user.getTenantId(), result);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.ERROR_GET_USED_CREDIT_AMOUNT, CustomerAccountErrorCode.ERROR_GET_USED_CREDIT_AMOUNT.getMessage());
        }
        log.info("getUsedCreditAmount,user:{},customerId:{},result:{}", user, customerId, JsonUtil.toJson(result));
        double usedCredit = result.getValue().getUsedCreditAmount();
        return new BigDecimal(usedCredit);
    }

    public List<IObjectData> listInvalidRefundByIds(User user, List<String> refundIds) {
        List<IObjectData> resultList = listInvalidDataByIds(user, SystemConstants.RefundApiName, refundIds);
        if (CollectionUtils.isEmpty(resultList)) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<IObjectData> listInvalidPaymentByIds(User user, List<String> paymentIds) {
        List<IObjectData> paymentResult = listInvalidDataByIds(user, SystemConstants.PaymentApiName, paymentIds);
        if (CollectionUtils.isEmpty(paymentResult)) {
            return Lists.newArrayList();
        }
        return paymentResult;
    }

    public List<IObjectData> listInvalidOrderPaymentByIds(User user, List<String> paymentIds) {
        List<IObjectData> paymentResult = listInvalidDataByIds(user, SystemConstants.OrderPaymentApiname, paymentIds);
        if (CollectionUtils.isEmpty(paymentResult)) {
            return Lists.newArrayList();
        }
        return paymentResult;
    }

    public List<IObjectData> listInvalidCustomerByIds(User user, List<String> customerIds) {
        List<IObjectData> customers = listInvalidDataByIds(user, SystemConstants.AccountApiName, customerIds);
        if (CollectionUtils.isEmpty(customers)) {
            return Lists.newArrayList();
        }
        return customers;
    }

    private List<IObjectData> listInvalidDataByIds(User user, String objectApiName, List<String> ids) {
        ActionContext actionContext = new ActionContext();
        actionContext.setEnterpriseId(user.getTenantId());
        actionContext.setUserId(user.getTenantId());
        try {
            List<IObjectData> resultList = serviceFacade.findObjectDataByIdsIncludeDeleted(user, ids, objectApiName);
            return resultList;
        } catch (Exception e) {
            log.warn("queryInvalidDataByField user:{},objectApiName:{},id{},offset:{},limit:{}", user, objectApiName, ids, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.METADATA_QUERY_ERROR, e.getMessage());
        }
    }

    private Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

}

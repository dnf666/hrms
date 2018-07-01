package com.facishare.crm.customeraccount.predefine.manager;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.exception.DataExpiredException;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.predefine.service.InitService;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.util.*;
import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.dto.ApprovalStatusEnum;
import com.facishare.crm.rest.dto.CrmCustomerVo;
import com.facishare.crm.rest.dto.GetCurInstanceStateModel;
import com.facishare.crm.rest.dto.QueryCustomersByPage;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.ErrorCode;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xujf on 2017/10/19.<br>
 * Manager是提供给service和Action调用的<br>
 */
@EnableRetry(proxyTargetClass = true)
@Component
@Slf4j
public class CustomerAccountManager extends CommonManager {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private InitService initService;
    @Autowired
    private CustomerAccountConfigManager customerAccountConfigManager;
    @Autowired
    private CrmManager crmManager;
    @Autowired
    private PrepayDetailManager prepayDetailManager;
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    private ApprovalInitProxy approvalInitProxy;
    @Autowired
    private CustomerAccountBillManager customerAccountBillManager;

    public BigDecimal getAvailabelCredit(User user, String customerId) {
        IObjectData customerAccount = this.getCustomerAccountByCustomerId(user, customerId);
        BigDecimal initCredit = ObjectDataUtil.getBigDecimal(customerAccount, CustomerAccountConstants.Field.CreditQuota.getApiName());
        //customerAccount.get(CustomerAccountConstants.Field.CreditQuota.getApiName(), BigDecimal.class);
        BigDecimal usedCredit = crmManager.getUsedCreditAmount(user, customerId);
        BigDecimal availabelCredit = initCredit.subtract(usedCredit);
        log.info("getAvailableCredit for customerId:{},initcredit:{},usedcredit:{},availableCredit:{}", customerId, initCredit, usedCredit, availabelCredit);
        return availabelCredit;
    }

    public IObjectData updateLifeStatus(User user, IObjectData customerAccountObjectData) {
        return update(user, customerAccountObjectData);
    }

    private IObjectData update(User user, IObjectData customerAccountObjectData) {
        IObjectData data = serviceFacade.updateObjectData(user, customerAccountObjectData, true);
        recordLog(user, customerAccountObjectData);
        return data;
    }

    /**
     * 更新返利相关金额
     *
     * @param user
     * @param customerId
     * @param addRebateBalance
     * @param addRebateLockedBalance
     * @return
     */
    @Retryable(value = {DataExpiredException.class}, maxAttempts = 10, backoff = @Backoff(delay = 500))
    public IObjectData updateRebateBalance(User user, String customerId, BigDecimal addRebateBalance, BigDecimal addRebateLockedBalance, String info, String rebateId) {
        if (addRebateBalance == null) {
            addRebateBalance = new BigDecimal(0);
        }
        if (addRebateLockedBalance == null) {
            addRebateLockedBalance = new BigDecimal(0);
        }
        IObjectData account = this.getCustomerAccountByCustomerId(user, customerId);
        BigDecimal rebateBalance = account.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class);
        BigDecimal rebateLockedBalance = account.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class);
        BigDecimal newRebateBalance = rebateBalance.add(addRebateBalance);
        BigDecimal newRebateLockedBalance = rebateLockedBalance.add(addRebateLockedBalance);
        BigDecimal newRebateAvailableBalance = newRebateBalance.subtract(newRebateLockedBalance);
        account.set(CustomerAccountConstants.Field.RebateBalance.apiName, newRebateBalance);
        account.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, newRebateLockedBalance);
        account.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, newRebateAvailableBalance);
        IObjectData result = null;
        try {
            result = this.update(user, account);
        } catch (MetaDataBusinessException e) {
            CustomerAccountRecordLogger.logRebate(customerId, addRebateBalance, addRebateLockedBalance, newRebateBalance, newRebateLockedBalance, info, e);
            if (e.getErrorCode() == ErrorCode.FS_PAAS_SPECIAL_TABLE_UPDATE_EXCEPTION.getCode()) {
                throw new DataExpiredException("update data version expired");
            }
            throw e;
        }
        try {
            CustomerAccountRecordLogger.logRebate(customerId, addRebateBalance, addRebateLockedBalance, newRebateBalance, newRebateLockedBalance, info, null);
            String customerAccountId = result.getId().toString();
            double addRebateDoubleValue = addRebateBalance.doubleValue();
            double addRebateLockDoubleValue = addRebateLockedBalance.doubleValue();
            customerAccountBillManager.addCustomerAccountBillAccordRebate(customerAccountId, rebateId, addRebateDoubleValue, addRebateLockDoubleValue, user.getTenantId(), info);
            return result;
        } catch (Exception e) {
            log.warn("addCustomerAccountBillAccordRebate exception");
            CustomerAccountRecordLogger.logRebate(customerId, addRebateBalance, addRebateLockedBalance, newRebateBalance, newRebateLockedBalance, info, e);
            throw e;
        }
    }

    /**
     * 更新预存款相关金额
     *
     * @param user
     * @param customerId
     * @param addPrepayBalance       总余额
     * @param addPrepayLockedBalance 锁定余额
     * @return
     */
    @Retryable(value = {DataExpiredException.class}, maxAttempts = 10, backoff = @Backoff(delay = 500))
    public IObjectData updatePrepayBalance(User user, String customerId, BigDecimal addPrepayBalance, BigDecimal addPrepayLockedBalance, String info, String prepayId) {
        if (addPrepayBalance == null) {
            addPrepayBalance = new BigDecimal(0);
        }
        if (addPrepayLockedBalance == null) {
            addPrepayLockedBalance = new BigDecimal(0);
        }
        IObjectData account = this.getCustomerAccountByCustomerId(user, customerId);
        BigDecimal prepayBalance = account.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class);
        BigDecimal prepayLockedBalance = account.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class);
        BigDecimal newPrepayBalance = prepayBalance.add(addPrepayBalance);
        BigDecimal newPrepayLockedBalance = prepayLockedBalance.add(addPrepayLockedBalance);

        BigDecimal newPrepayAvailableBalance = newPrepayBalance.subtract(newPrepayLockedBalance);
        account.set(CustomerAccountConstants.Field.PrepayBalance.apiName, newPrepayBalance);
        account.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, newPrepayLockedBalance);
        account.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, newPrepayAvailableBalance);
        IObjectData result = null;
        try {
            result = this.update(user, account);
        } catch (MetaDataBusinessException e) {
            CustomerAccountRecordLogger.logPrepay(customerId, addPrepayBalance, addPrepayLockedBalance, newPrepayBalance, newPrepayLockedBalance, info, e);
            if (e.getErrorCode() == ErrorCode.FS_PAAS_SPECIAL_TABLE_UPDATE_EXCEPTION.getCode()) {
                throw new DataExpiredException("update data version expired");
            }
            throw e;
        }
        try {
            CustomerAccountRecordLogger.logPrepay(customerId, addPrepayBalance, addPrepayLockedBalance, newPrepayBalance, newPrepayLockedBalance, info, null);
            String customerAccountId = result.getId().toString();
            customerAccountBillManager.addCustomerAccountBillAccordPrepay(customerAccountId, prepayId, addPrepayBalance.doubleValue(), addPrepayLockedBalance.doubleValue(), user.getTenantId(), info);
            return result;
        } catch (Exception e) {
            log.warn("addCustomerAccountBillAccordPrepay exception");
            CustomerAccountRecordLogger.logPrepay(customerId, addPrepayBalance, addPrepayLockedBalance, newPrepayBalance, newPrepayLockedBalance, info, e);
            throw e;
        }
    }

    public List<IObjectData> batchInitCustomerAccountDatas(User user, Map<String, String> customerIdStatusMap) {
        List<String> customerIds = Lists.newArrayList(customerIdStatusMap.keySet());
        List<IObjectData> oldCustomerAccounts = this.listCustomerAccountIncludeInvalidByCustomerIds(user, customerIds);
        for (IObjectData oldData : oldCustomerAccounts) {
            log.info("had oldCustomerAccount,oldData={}", oldData.toJsonString());
            String cusId = ObjectDataUtil.getReferenceId(oldData, CustomerAccountConstants.Field.Customer.apiName);
            customerIds.remove(cusId);
        }
        List<IObjectData> customerAccounts = new ArrayList<IObjectData>(customerIds.size());
        List<String> invalidCustomerIds = customerIds.stream().filter(customerId -> SystemConstants.LifeStatus.Invalid.value.equals(customerIdStatusMap.get(customerId))).collect(Collectors.toList());
        Map<String, String> customerLifeStatusBeforeInvalidMap = getCustomerLifeStatusBeforeInvalid(user, invalidCustomerIds);
        for (String customerId : customerIds) {
            String lifeStatus = customerIdStatusMap.get(customerId);
            IObjectData data = generateCustomerAccountInitData(user, customerId, lifeStatus);
            if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                String lifeStatusBeforeInvalid = customerLifeStatusBeforeInvalidMap.get(customerId);
                data.set("life_status_before_invalid", lifeStatusBeforeInvalid);
            }
            customerAccounts.add(data);
        }
        List<IObjectData> result = Lists.newArrayList();
        if (CollectionUtils.notEmpty(customerAccounts)) {
            List<IObjectData> tmpList = this.serviceFacade.bulkSaveObjectData(customerAccounts, user);
            List<IObjectData> toInvalidDatas = tmpList.stream().filter(objectData -> {
                String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    return true;
                } else {
                    result.add(objectData);
                    return false;
                }
            }).collect(Collectors.toList());
            if (CollectionUtils.notEmpty(toInvalidDatas)) {
                log.info("batchInitCustomerAccountDatas ,user:{},invalidDatas:{}", user, JsonUtil.toJson(toInvalidDatas));
                List<IObjectData> invalidDatas = serviceFacade.bulkInvalid(toInvalidDatas, user);
                result.addAll(invalidDatas);
            }
        }
        if (!oldCustomerAccounts.isEmpty()) {
            result.addAll(oldCustomerAccounts);
        }
        return result;
    }

    private IObjectData generateCustomerAccountInitData(User user, String customerId, String lifeStatus) {
        try {
            IObjectData objectData = new ObjectData();
            objectData.setDescribeApiName(CustomerAccountConstants.API_NAME);
            IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(user.getTenantId(), CustomerAccountConstants.API_NAME);
            objectData.setDescribeId(objectDescribe.getId());
            objectData.setCreatedBy(user.getUserId());
            objectData.setLastModifiedBy(user.getUserId());
            objectData.setDeleted(false);
            objectData.setTenantId(user.getTenantId());
            objectData.setRecordType("default__c");
            objectData.set(CustomerAccountConstants.Field.Customer.apiName, customerId);
            objectData.set(CustomerAccountConstants.Field.PrepayBalance.apiName, 0);
            objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, 0);
            objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, 0);
            objectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, 0);
            objectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, 0);
            objectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, 0);
            //创建客户 同步创建客户账户，结算方式默认是先付。
            objectData.set(CustomerAccountConstants.Field.SettleType.apiName, Lists.newArrayList(SettleTypeEnum.Cash.getValue()));
            objectData.set(CustomerAccountConstants.Field.CreditQuota.apiName, 0);
            objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(user.getUserId()));
            objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(user.getUserId()));
            objectData.set(SystemConstants.Field.LockStatus.apiName, "0");
            objectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            objectData = fillDefaultObject(user, objectDescribe, objectData);
            return objectData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量初始化客户账户，如果客户已经初始化
     *
     * @param tenantId
     * @param
     */
    public boolean batchInitCustomerAccounts(String tenantId) {
        boolean success = true;
        User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
        int totalInitCustomerAccount = 0;
        try {
            CustomerAccountType.CustomerAccountEnableSwitchStatus status = customerAccountConfigManager.getStatus(tenantId);
            if (status != CustomerAccountType.CustomerAccountEnableSwitchStatus.UNABLE && status != CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING_LATER && status != CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING) {
                log.info("customer account have been inited before,for customerAccountEnableSwtichStatus:{}", status);
                success = false;
                return false;
            }
            //初始化描述
            log.info("begin init describe,for tenantId:{}", user.getTenantId());
            ServiceContext serviceContext = RequestUtil.generateServiceContextByTenantId(user.getTenantId(), user.getUserId());
            initService.init(serviceContext);
            //初始化客户账户
            int offset = 0;
            int limit = ConfigCenter.batchCreateSize;
            int fetchSize = 0;
            do {
                Map<String, Integer> customerIdStatusMap;
                if (ConfigCenter.queryCustomersFormPg) {
                    List<IObjectData> customerObjectDatas = crmManager.listPlainCustomersFromPg(user, null, offset, limit);
                    customerIdStatusMap = customerObjectDatas.stream().collect(Collectors.toMap(cusObjectData -> ObjectDataUtil.getReferenceId(cusObjectData, CustomerAccountConstants.Field.Customer.apiName), cob -> cob.get("status", Integer.class)));
                } else {
                    QueryCustomersByPage.Result result = crmManager.queryCustomers(user, offset, limit);
                    if (!result.isSuccess()) {
                        return false;
                    }
                    List<CrmCustomerVo> onePageCustomers = result.getValue().getItems();
                    customerIdStatusMap = onePageCustomers.stream().collect(Collectors.toMap(CrmCustomerVo::getCustomerID, CrmCustomerVo::getStatus));
                }
                Map<String, String> customerIdLifeStatusMap = transferCustomerStatusToLifeStatus(user, customerIdStatusMap);
                try {
                    batchInitCustomerAccountDatas(user, customerIdLifeStatusMap);
                } catch (Exception e) {
                    success = false;
                    log.warn("customerIdStatusMap=" + customerIdStatusMap, e);
                }
                fetchSize = customerIdStatusMap.size();
                totalInitCustomerAccount += fetchSize;
                offset += limit;
            } while (fetchSize == limit);
            //初始化crm 客戶账户开关
            if (success) {
                boolean crmSuccess = crmManager.syncCustomerAccountSwitchToCrm(user);
                if (!crmSuccess) {
                    success = false;
                }
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            if (success) {
                customerAccountConfigManager.updateStatus(user, CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE);
                //下发消息
                try {
                    CustomerAccountConfig customerAccountConfig = customerAccountConfigManager.getCutomerAccountConfig(tenantId);
                    int i = 0;
                    while (i < 4) {
                        Boolean result = addRemindRecord(tenantId, customerAccountConfig.getCreateBy(), totalInitCustomerAccount);
                        if (result == Boolean.TRUE) {
                            break;
                        }
                        i++;
                    }
                } catch (Exception e) {
                    log.warn("addRemindRecord error,tenantId:{}", tenantId, e);
                }
            } else {
                customerAccountConfigManager.updateStatus(user, CustomerAccountType.CustomerAccountEnableSwitchStatus.FAILED);
            }
        }
        return success;
    }

    public Map<String, String> getCustomerLifeStatusBeforeInvalid(User user, List<String> customerIds) {
        Map<String, Integer> customerStatusBeforeInvalidMap;
        try {
            customerStatusBeforeInvalidMap = crmManager.listCustomerStatusBeforeInvalid(user, customerIds);
        } catch (Exception e) {
            log.warn("getCustomerLifeStatusBeforeInvalid ,user:{},customerIds:{}", user, customerIds, e);
            customerStatusBeforeInvalidMap = Maps.newHashMap();
        }
        Map<String, String> customerLifeStatusBeforeInvalidMap = Maps.newHashMap();
        for (String customerId : customerIds) {
            Integer customerStatus = customerStatusBeforeInvalidMap.get(customerId);
            String lifeStatus;
            if (customerStatus != null) {
                if (customerStatus == 2 || customerStatus == 3) {
                    lifeStatus = SystemConstants.LifeStatus.Normal.value;
                } else {
                    lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
                }
            } else {
                lifeStatus = SystemConstants.LifeStatus.Normal.value;
            }
            customerLifeStatusBeforeInvalidMap.put(customerId, lifeStatus);
        }
        return customerLifeStatusBeforeInvalidMap;
    }

    public Map<String, String> transferCustomerStatusToLifeStatus(User user, Map<String, Integer> customerIdStatusMap) {
        Map<String, String> customerIdLifeStatusMap = Maps.newHashMap();
        List<String> notInvalidcustomerIds = customerIdStatusMap.entrySet().stream().filter(entry -> {
            Integer customerStatus = entry.getValue();
            if (customerStatus != 99) {
                return true;
            } else {
                customerIdLifeStatusMap.put(entry.getKey(), SystemConstants.LifeStatus.Invalid.value);
                return false;
            }
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        log.info("transferCustomerStatusToLifeStatus notInvalidcustomerIds:{},otherCustomerIds:{}", notInvalidcustomerIds, customerIdLifeStatusMap.keySet());
        if (CollectionUtils.empty(notInvalidcustomerIds)) {
            return customerIdLifeStatusMap;
        }
        GetCurInstanceStateModel.Arg arg = new GetCurInstanceStateModel.Arg();
        arg.setObjectIds(notInvalidcustomerIds);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", user.getUserId());
        headers.put("x-tenant-id", user.getTenantId());
        GetCurInstanceStateModel.Result getCurInstancesStateResult = approvalInitProxy.getCurInstanceStateByObjectIds(arg, headers);
        log.info("getCurInstancesStateResult headers:{},arg:{},result:{}", headers, arg, getCurInstancesStateResult);
        if (!getCurInstancesStateResult.success()) {
            notInvalidcustomerIds.forEach(customerIdNotInvalud -> {
                customerIdLifeStatusMap.put(customerIdNotInvalud, SystemConstants.LifeStatus.Ineffective.value);
            });
            return customerIdLifeStatusMap;
        }
        List<GetCurInstanceStateModel.IntanceStatus> intanceStatusList = getCurInstancesStateResult.getData();
        if (CollectionUtils.empty(intanceStatusList)) {
            notInvalidcustomerIds.forEach(customerId -> {
                Integer customerStatus = customerIdStatusMap.get(customerId);
                String lifeStatus = customerStatusToLifeStatus(customerStatus);
                customerIdLifeStatusMap.put(customerId, lifeStatus);
            });
        } else {
            Map<String, GetCurInstanceStateModel.IntanceStatus> intanceStatusMap = intanceStatusList.stream().collect(Collectors.toMap(GetCurInstanceStateModel.IntanceStatus::getObjectId, in -> in));
            for (String customerId : notInvalidcustomerIds) {
                String lifeStatus;
                Integer customerStatus = customerIdStatusMap.get(customerId);
                GetCurInstanceStateModel.IntanceStatus intanceStatus = intanceStatusMap.get(customerId);
                if (Objects.isNull(intanceStatus)) {
                    lifeStatus = customerStatusToLifeStatus(customerStatus);
                    customerIdLifeStatusMap.put(customerId, lifeStatus);
                } else {
                    String triggerType = intanceStatus.getTriggerType();
                    String approvalStatus = intanceStatus.getStatus();
                    switch (customerStatus) {
                        case 1:
                            if (ApprovalFlowTriggerType.CREATE.getId().equals(triggerType) && ApprovalStatusEnum.IN_PROGRESS.getValue().equals(approvalStatus)) {
                                lifeStatus = SystemConstants.LifeStatus.UnderReview.value;
                            } else {
                                lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
                            }
                            break;
                        case 2:
                        case 3:
                            if (ApprovalFlowTriggerType.INVALID.getId().equals(triggerType) && ApprovalStatusEnum.IN_PROGRESS.getValue().equals(approvalStatus)) {
                                lifeStatus = SystemConstants.LifeStatus.InChange.value;
                            } else {
                                lifeStatus = SystemConstants.LifeStatus.Normal.value;
                            }
                            break;
                        case 99:
                            //已被过滤了
                            lifeStatus = SystemConstants.LifeStatus.Invalid.value;
                            log.warn("impossible status,customerId:{},customerStatus:{},lifeStatus:{}", customerId, customerStatus, lifeStatus);
                            break;
                        default:
                            //不存在
                            lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
                            log.warn("impossible status,customerId:{},customerStatus:{},lifeStatus:{}", customerId, customerStatus, lifeStatus);
                            break;
                    }
                    customerIdLifeStatusMap.put(customerId, lifeStatus);
                }
            }
        }
        log.info("transferCustomerStatusToLifeStatus user:{},customerIdLifeStatusMap:{}", user, customerIdLifeStatusMap);
        return customerIdLifeStatusMap;
    }

    private String customerStatusToLifeStatus(int customerStatus) {
        String lifeStatus;
        switch (customerStatus) {
            case 1:
                lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
                break;
            case 2:
            case 3:
                lifeStatus = SystemConstants.LifeStatus.Normal.value;
                break;
            case 99:
                lifeStatus = SystemConstants.LifeStatus.Invalid.value;
                break;
            default:
                lifeStatus = SystemConstants.LifeStatus.Ineffective.value;
                break;
        }
        return lifeStatus;
    }

    public Boolean addRemindRecord(String tenantId, String userId, Integer customerAccountNumbers) {
        String url = ConfigCenter.crmUrl + "/AddRemindRecord";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", User.SUPPER_ADMIN_USER_ID);
        headers.put("Content-Type", "application/json");

        String content = "已成功为" + customerAccountNumbers + "个客户创建了客户账户，客户账户初始化完成，请开始您的业务处理。";
        Map<String, Object> body = new HashMap<>();
        body.put("employeeID", User.SUPPER_ADMIN_USER_ID);
        body.put("remindRecordType", 89);
        body.put("content", content);
        body.put("receiverIDs", Lists.newArrayList(userId));
        body.put("title", "");
        body.put("dataID", "");
        body.put("fixContent2ID", "");
        String json = JsonUtil.toJson(body);
        Type type = TypeUtils.parameterize(CustomerAccountType.RemindRecordResult.class);
        CustomerAccountType.RemindRecordResult remindRecordResult = null;
        try {
            remindRecordResult = HttpUtil.post(url, headers, json, type);
            if (!remindRecordResult.getSuccess()) {
                log.info("remindrecord message send failed.headers:{},body:{},remindrecordResult:{}", headers, body, remindRecordResult);
            }
        } catch (IOException e) {
            log.warn("addRemindRecord error,tenantId:{},headers:{},body:{}", tenantId, headers, body, e);
        }
        return remindRecordResult.getSuccess();
    }

    /**
     * 根据客户Id获取客户账户<br>
     * * @param user
     *
     * @param customerId
     * @return
     */
    public IObjectData getCustomerAccountByCustomerId(User user, String customerId) {
        IFilter filter = new Filter();
        String apiName = CustomerAccountConstants.Field.Customer.getApiName();
        filter.setFieldName(apiName);
        filter.setFieldValues(Lists.newArrayList(customerId));
        filter.setOperator(Operator.EQ);
        List<IObjectData> dataList = serviceFacade.findDataWithWhere(user, CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, 10);
        if (CollectionUtils.empty(dataList)) {
            String message = "CustomerId =" + customerId + " 的客戶账户记录不存在。";
            log.info(message);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_ACCOUNT_NOT_EXIST, CustomerAccountErrorCode.CUSTOMER_ACCOUNT_NOT_EXIST.getMessage());
        }
        return dataList.get(0);
    }

    public Optional<IObjectData> getCustomerAccountIncludeInvalidByCustomerId(User user, String customerId) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, CustomerAccountConstants.API_NAME, CustomerAccountConstants.Field.Customer.apiName, Lists.newArrayList(customerId), 0, 1);
        if (CollectionUtils.empty(queryResult.getData())) {
            return Optional.empty();
        }
        return Optional.of(queryResult.getData().get(0));
    }

    public List<IObjectData> listCustomerAccountByCustomerIds(User user, List<String> customerIds) {
        IFilter filter = new Filter();
        String apiName = CustomerAccountConstants.Field.Customer.getApiName();
        filter.setFieldName(apiName);
        filter.setFieldValues(customerIds);
        filter.setOperator(Operator.IN);
        List<IObjectData> dataList = serviceFacade.findDataWithWhere(user, CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, customerIds.size());
        if (dataList == null) {
            return Lists.newArrayList();
        }
        return dataList;
    }

    public List<IObjectData> listCustomerAccountIncludeInvalidByCustomerIds(User user, List<String> customerIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, CustomerAccountConstants.API_NAME, CustomerAccountConstants.Field.Customer.apiName, customerIds, 0, customerIds.size());
        if (CollectionUtils.empty(queryResult.getData())) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }

    public CustomerAccountType.CanInvalidByCustomerIdsResult canInvalidByCustomerIds(User user, CustomerAccountType.CanInvalidByCustomerIdsArg canInvalidByCustomerIdsArg) {
        List<String> customerIds = canInvalidByCustomerIdsArg.getCustomerIds();
        List<IObjectData> customerAccouontObjectDataList = listCustomerAccountByCustomerIds(user, customerIds);
        Map<String, IObjectData> customerAccountObjectDataMap = customerAccouontObjectDataList.stream().collect(Collectors.toMap(objectData -> ObjectDataUtil.getReferenceId(objectData, CustomerAccountConstants.Field.Customer.apiName), ob -> ob));

        CustomerAccountType.CanInvalidByCustomerIdsResult canInvalidByCustomerIdsResult = new CustomerAccountType.CanInvalidByCustomerIdsResult();
        canInvalidByCustomerIdsResult.setSuccess(true);
        Map<String, String> errorReasons = new HashMap<String, String>();
        for (String customerId : customerIds) {
            CustomerAccountType.GetByCustomerIdArg getByCustomerIdArg = new CustomerAccountType.GetByCustomerIdArg();
            getByCustomerIdArg.setCustomerId(customerId);
            String errorReasonStr = null;
            IObjectData iObjectData = customerAccountObjectDataMap.get(customerId);
            if (iObjectData == null) {
                throw new ValidateException("客户账户不存在");
            } else {
                errorReasonStr = canInvalidCustomerAccount(user, iObjectData);
                if (errorReasonStr != null) {
                    log.info("customeraccount cannot invalid,for customerid:{},errrorReason:{}", customerId, errorReasonStr);
                    canInvalidByCustomerIdsResult.setSuccess(false);
                    errorReasons.put(customerId, errorReasonStr);
                }
            }
        }
        canInvalidByCustomerIdsResult.setErrorReasons(errorReasons);
        return canInvalidByCustomerIdsResult;
    }

    /**
     * 客户账户余额变更情况如下：<br>
     * 1.客户账户 关联的预存款、返利收入、返利支出，如果其审批流程都走完了，其总余额=可用余额。此情况下如果余额=0 则可以作废。<br>
     * 2.在有未完成的审批流程的情况下，对于支出减钱操作，其锁定余额肯定不为0，此种情况如果锁定不为0则不能作废。<br>
     * 3.在有未完成的审批流程的情况下，对于收入操作需要一个个遍历收入明细（预存款、返利）查看是否是在审批中的状态。<br>
     * 4.在有未完成审批流程的情况下，对于支出作废这种加钱的情况，比如预存款支出作废，返利支出作废。有normal->incharge,其总余额和锁定余额都会增加。此时只需要判断总余额不为0则不可作废。<br>
     * 5.返利支出流程判断会在SFA回款那一段就已经判断了,在此不用重复判断。<br>
     *
     * @param iObjectData
     * @return
     */
    public String canInvalidCustomerAccount(User user, IObjectData iObjectData) {

        BigDecimal prepayBalance = iObjectData.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class);
        BigDecimal rebateBalance = iObjectData.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class);

        BigDecimal prepayLockBalance = iObjectData.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class);
        BigDecimal rebateLockBalance = iObjectData.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class);

        String customerId = iObjectData.get(CustomerAccountConstants.Field.Customer.apiName, String.class);

        String errorReason = null;
        if (prepayBalance.compareTo(BigDecimal.ZERO) == 1) {
            errorReason = "预存款金额不为零，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);

            return errorReason;
        }
        if (rebateBalance.compareTo(BigDecimal.ZERO) == 1) {
            errorReason = "返利金额不为零，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);

            return errorReason;
        }
        if (prepayLockBalance.compareTo(BigDecimal.ZERO) == 1) {
            errorReason = "存在未确认的预存款明细，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);

            return errorReason;
        }
        if (rebateLockBalance.compareTo(BigDecimal.ZERO) == 1) {
            errorReason = "存在未确认的返利明细，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);

            return errorReason;
        }

        List<IObjectData> unfinishedPrepayObj = prepayDetailManager.listUnfinishedPrepayDetailByCustomerId(user, customerId);
        if (!unfinishedPrepayObj.isEmpty()) {
            errorReason = "存在未确认的预存款明细，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);
            return errorReason;
        }
        List<IObjectData> unfinishedRebateIncomeObj = rebateIncomeDetailManager.listUnfinishedRebateIncomeDetailByCustomerId(user, customerId);
        if (!unfinishedRebateIncomeObj.isEmpty()) {
            errorReason = "存在未确认的返利明细，无法作废！";
            log.info("erroReason:{},customerId:{}", errorReason, customerId);
            return errorReason;
        }

        //因为没有手动创建的返利支出，所以返利支出流程判断会在SFA回款那一段就已经判断了,在此不用重复判断。<br>

        return errorReason;
    }

    public List<IObjectData> listInvalidDataByIds(User user, List<String> dataIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, CustomerAccountConstants.API_NAME, IObjectData.ID, dataIds, 0, 1);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(queryResult.getData())) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }
}

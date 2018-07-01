package com.facishare.crm.customeraccount.predefine.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.predefine.action.RebateIncomeDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountBillManager;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountConfigManager;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.PrepayDetailService;
import com.facishare.crm.customeraccount.predefine.service.RebateIncomeDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.BulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType.SettleType;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.predefine.service.dto.FlowCompleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.FlowCompleteModel.Arg;
import com.facishare.crm.customeraccount.predefine.service.dto.FlowCompleteModel.Result;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.crm.customeraccount.util.AssertUtil;
import com.facishare.crm.customeraccount.util.CommonPoolUtil;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectLockAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/9/25.
 */
@Slf4j
@Component
public class CustomerAccountServiceImpl extends CommonService implements CustomerAccountService {
    @Autowired
    private CustomerAccountManager customerAccountManager;
    @Autowired
    private CustomerAccountConfigManager customerAccountConfigManager;
    @Autowired
    private CustomerAccountConfigDao customerAccountConfigDao;
    @Autowired
    private CrmManager crmManager;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private PrepayDetailService prepayDetailService;
    @Autowired
    private RebateIncomeDetailService rebateIncomeDetailService;

    @Autowired
    private CustomerAccountBillManager customerAccountBillManager;

    /**
     * 调用config模块<br>
     * @param serviceContext
     * @return
     */
    @Override
    public CustomerAccountType.IsCustomerAccountEnableResult isCustomerAccountEnable(ServiceContext serviceContext) {
        String tenantId = serviceContext.getTenantId();
        CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableSwitchStatus = customerAccountConfigManager.getStatus(tenantId);
        CustomerAccountType.IsCustomerAccountEnableResult result = new CustomerAccountType.IsCustomerAccountEnableResult();
        if (customerAccountEnableSwitchStatus.getValue() != CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE.getValue()) {
            //除了enable 状态，其他都返回false<br>
            log.info("customer account switch status is not enable,for status:{}", customerAccountEnableSwitchStatus.getValue());
            result.setEnable(false);
        } else {
            result.setEnable(true);
        }
        return result;
    }

    /**
     * 信用+可用余额>订单金额则该订单可以创建<br>
     * 否则不可以创建订单<br>
     * 1.信用开启的情况填：预付款+返利+信用
     * 2.信用不开启：任何情况下都可以下单。
     *
     * @return
     */
    @Override
    public CustomerAccountType.BalanceCreditEnoughResult isBalanceAndCreditEnough(ServiceContext serviceContext, CustomerAccountType.OrderArg orderArg) {
        CustomerAccountType.BalanceCreditEnoughResult returnResult = new CustomerAccountType.BalanceCreditEnoughResult();
        AssertUtil.argumentNotNullOrEmpty("orderArg.customerId", orderArg.getCustomerId());
        User user = serviceContext.getUser();
        String customerId = orderArg.getCustomerId();
        //可用信用
        BigDecimal availableCredit = customerAccountManager.getAvailabelCredit(user, customerId);
        //可用余额
        IObjectData customerAccount = customerAccountManager.getCustomerAccountByCustomerId(user, customerId);
        BigDecimal availablePrepayBalance = customerAccount.get(CustomerAccountConstants.Field.PrepayAvailableBalance.getApiName(), BigDecimal.class);
        BigDecimal availableRebateBalance = customerAccount.get(CustomerAccountConstants.Field.RebateAvailableBalance.getApiName(), BigDecimal.class);

        BigDecimal minusAmount = BigDecimal.valueOf(orderArg.getOrderAmount()).subtract(BigDecimal.valueOf(orderArg.getOldOrderAmount()));
        if (minusAmount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            returnResult.setEnough(true);
            return returnResult;
        }

        BigDecimal availableBalance = availablePrepayBalance.add(availableRebateBalance);
        log.info("customerAccounttype->可用金额校验：availableCredit:{},availalbePrepayBalance:{},availableRebateBalance:{},orderArg:{}", availableCredit, availablePrepayBalance, availableRebateBalance, orderArg);
        if (SettleTypeEnum.Prepay.getValue().equals(orderArg.getSettleType())) {
            //如果信用未开启，表示信用无穷大，永远可以下单
            boolean isCreditEnable = customerAccountConfigManager.isCreditEnable(serviceContext.getTenantId());
            if (!isCreditEnable) {
                //没有开启信用的情况下，不校验信用，所以设置为0。
                availableCredit = BigDecimal.ZERO;
            }
            /**
             * 未开启情况<br>
             */
            if (availableCredit.add(availableBalance).compareTo(minusAmount) >= 0) {
                returnResult.setEnough(true);
            } else {
                returnResult.setEnough(false);
            }
        } else if (SettleTypeEnum.Credit.getValue().equals(orderArg.getSettleType())) {
            // 信用不开启的情况下，订单页面选择不了赊销的方式。
            if (availableCredit.compareTo(minusAmount) >= 0) {
                returnResult.setEnough(true);
            } else {
                returnResult.setEnough(false);
            }
        } else if (SettleTypeEnum.Cash.getValue().equals(orderArg.getSettleType())) {
            //现付的情况不会校验余额,所以永远返回成功的
            returnResult.setEnough(true);
        }
        return returnResult;
    }

    @Override
    public CustomerAccountType.BalanceEnoughResult isBalanceEnough(ServiceContext serviceContext, CustomerAccountType.PaymentArg paymentArg) {
        CustomerAccountType.GetByCustomerIdArg arg = new CustomerAccountType.GetByCustomerIdArg();
        arg.setCustomerId(paymentArg.getCustomerId());
        Map<String, Object> customerAccountObj = getByCustomerId(serviceContext, arg).getObjectData();
        CustomerAccountType.BalanceEnoughResult returnResult = new CustomerAccountType.BalanceEnoughResult();

        double availablePrepayBalance = Double.parseDouble(customerAccountObj.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName).toString());
        double availableRebateBalance = Double.parseDouble(customerAccountObj.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName).toString());
        if (paymentArg.getPrepayToPay() <= availablePrepayBalance) {
            returnResult.setPrepayEnough(true);
        } else {
            returnResult.setPrepayEnough(false);
        }
        if (paymentArg.getRebateToPay() <= availableRebateBalance) {
            returnResult.setRebateEnough(true);
        } else {
            returnResult.setRebateEnough(false);
        }
        return returnResult;
    }

    @Override
    public CustomerAccountType.CanInvalidByCustomerIdsResult canInvalidByCustomerIds(ServiceContext serviceContext, CustomerAccountType.CanInvalidByCustomerIdsArg canInvalidByCustomerIdsArg) {
        return customerAccountManager.canInvalidByCustomerIds(serviceContext.getUser(), canInvalidByCustomerIdsArg);
    }

    @Override
    public CustomerAccountType.GetByCustomerIdResult getByCustomerId(ServiceContext serviceContext, CustomerAccountType.GetByCustomerIdArg customerIdArg) {
        IObjectData objectData = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), customerIdArg.getCustomerId());
        CustomerAccountType.GetByCustomerIdResult getByCustomerIdResult = new CustomerAccountType.GetByCustomerIdResult();
        getByCustomerIdResult.setObjectData(ObjectDataExt.toMap(objectData));
        return getByCustomerIdResult;
    }

    @Override
    public CustomerAccountType.IsCreditEnableResult isCreditEnable(ServiceContext serviceContext) {
        CustomerAccountType.IsCreditEnableResult isCreditEnableResult = new CustomerAccountType.IsCreditEnableResult();
        boolean isCreditEnable = customerAccountConfigManager.isCreditEnable(serviceContext.getTenantId());
        isCreditEnableResult.setEnable(isCreditEnable);
        return isCreditEnableResult;
    }

    @Override
    public CustomerAccountType.GetAvailableCreditResult getAvailableCredit(ServiceContext serviceContext, CustomerAccountType.GetAvailableCreditArg getAvailableCreditArg) {
        String customerId = getAvailableCreditArg.getCustomerId();
        User user = serviceContext.getUser();
        CustomerAccountType.GetAvailableCreditResult getAvailableCreditResult = new CustomerAccountType.GetAvailableCreditResult();

        BigDecimal availabelCredit = customerAccountManager.getAvailabelCredit(user, customerId);

        getAvailableCreditResult.setAvailableCredit(availabelCredit.doubleValue());
        return getAvailableCreditResult;
    }

    /**
     * 该接口是：获取其实是把3个接口合起来了，1).获取可用信用  2).信用是否开启  3).获取客户账户信息
     * @param serviceContext
     * @param getCustomerAccountAndCreditInfoArg
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public CustomerAccountType.GetCustomerAccountAndCreditInfoResult getCustomerAccountAndCreditInfo(ServiceContext serviceContext, CustomerAccountType.GetCustomerAccountAndCreditInfoArg getCustomerAccountAndCreditInfoArg) {
        CustomerAccountType.GetCustomerAccountAndCreditInfoResult result = new CustomerAccountType.GetCustomerAccountAndCreditInfoResult();
        CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableSwitchStatus = customerAccountConfigManager.getStatus(serviceContext.getTenantId());
        if (customerAccountEnableSwitchStatus != CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE) {
            return result;
        }
        User user = serviceContext.getUser();
        String customerId = getCustomerAccountAndCreditInfoArg.getCustomerId();
        //信用是否开启
        boolean isCreditEnable = customerAccountConfigManager.isCreditEnable(user.getTenantId());
        result.setCreditEnable(isCreditEnable);
        //可用信用
        if (isCreditEnable) {
            BigDecimal availableCredit = customerAccountManager.getAvailabelCredit(user, customerId);
            result.setAvailableCredit(availableCredit.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        //余额
        IObjectData customerAccount = customerAccountManager.getCustomerAccountByCustomerId(user, customerId);
        List<String> settleTypes = (List<String>) customerAccount.get(CustomerAccountConstants.Field.SettleType.apiName);
        List<SettleType> settleTypeEnumList = getSettleTypeList(serviceContext, settleTypes);
        BigDecimal prepayAvailableBalance = ObjectDataUtil.getBigDecimal(customerAccount, CustomerAccountConstants.Field.PrepayAvailableBalance.apiName);
        BigDecimal availableRebateAmount = ObjectDataUtil.getBigDecimal(customerAccount, CustomerAccountConstants.Field.RebateAvailableBalance.apiName);
        BigDecimal creditQuota = ObjectDataUtil.getBigDecimal(customerAccount, CustomerAccountConstants.Field.CreditQuota.apiName);
        result.setAvailablePrepayAmount(prepayAvailableBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        result.setAvailableRebateAmount(availableRebateAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        result.setCreditQuota(creditQuota.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        result.setSettleTypeEnumList(settleTypeEnumList);
        return result;
    }

    private List<CustomerAccountType.SettleType> getSettleTypeList(ServiceContext serviceContext, List<String> settleTypes) {
        Set<String> settleTyps = Sets.newHashSet(settleTypes);
        List<CustomerAccountType.SettleType> settleTypeList = new ArrayList<CustomerAccountType.SettleType>();
        for (SettleTypeEnum settleTypeObj : SettleTypeEnum.values()) {
            if (settleTyps.contains(settleTypeObj.getValue())) {
                //如果信用没有启用则需要把赊销从list中去掉(不加入list中)<br>
                if (!isCreditEnable(serviceContext).isEnable() && SettleTypeEnum.Credit.equals(SettleTypeEnum.getByValue(settleTypeObj.getValue()))) {
                    log.info("credit is not enable,for tenantId:{}", serviceContext.getTenantId());
                    continue;
                }
                CustomerAccountType.SettleType settleType = new CustomerAccountType.SettleType();
                settleType.setValue(settleTypeObj.getValue());
                settleType.setLabel(settleTypeObj.getLabel());
                settleType.setNotUsable(settleTypeObj.getNotUsable());
                settleTypeList.add(settleType);
            }
        }
        return settleTypeList;
    }

    /**
     *
     * 1.初始化描述<br>
     * 2.初始化数据<br>
     * 3.设置开关<br>
     * @param serviceContext
     * @return
     */
    @Override
    public CustomerAccountType.EnableCustomerAccountResult enableCustomerAccount(ServiceContext serviceContext) {
        CustomerAccountType.EnableCustomerAccountResult enableCustomerAccountResult = new CustomerAccountType.EnableCustomerAccountResult();
        CustomerAccountType.CustomerAccountEnableSwitchStatus status = customerAccountConfigManager.getStatus(serviceContext.getTenantId());
        if (status != CustomerAccountType.CustomerAccountEnableSwitchStatus.UNABLE) {
            enableCustomerAccountResult.setEnableStatus(status.getValue());
            if (status == CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING) {
                String messge = String.format("客户数%s超过限制，将在凌晨进行初始化", ConfigCenter.initInBehindThreshold);
                enableCustomerAccountResult.setMessage(messge);
            } else if (status == CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING_LATER) {
                String message = "正在初始化中，请2小时后再查看";
                log.debug(message + ",for tenantId:{}", serviceContext.getTenantId());
                enableCustomerAccountResult.setMessage(message);
            } else {
                String message = "已开启失败，请联系纷享客服！";
                log.debug(message + ",for tenantId:{}", serviceContext.getTenantId());
                enableCustomerAccountResult.setMessage(message);
            }
            return enableCustomerAccountResult;
        }
        Set<String> existDispalyNames = checkDisplayName(serviceContext.getTenantId());
        if (CollectionUtils.isNotEmpty(existDispalyNames)) {
            enableCustomerAccountResult.setMessage(Joiner.on(",").join(existDispalyNames).concat("名称已存在"));
            enableCustomerAccountResult.setEnableStatus(CustomerAccountType.CustomerAccountEnableSwitchStatus.FAILED.getValue());
            return enableCustomerAccountResult;
        }
        Integer totalCount = crmManager.getAllCustomerCount(serviceContext.getUser());
        if (totalCount >= ConfigCenter.initInBehindThreshold) {
            log.info("enable customerAccount in schedule,because totalcount >threshold,for totalcount:{},threshold{},tenantId:{},postId:{}", totalCount, ConfigCenter.initInBehindThreshold, serviceContext.getTenantId(), serviceContext.getPostId());
            customerAccountConfigManager.updateStatus(serviceContext.getUser(), CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING);
            enableCustomerAccountResult.setEnableStatus(CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING.getValue());
            enableCustomerAccountResult.setMessage(String.format("客户数超过限制{%s}，将在凌晨进行初始化，待完成后系统将发送CRM通知", ConfigCenter.initInBehindThreshold));
        } else {
            log.info("enable customerAccount right now. for tenantId:{},postId:{}", serviceContext.getTenantId(), serviceContext.getPostId());
            customerAccountConfigManager.updateStatus(serviceContext.getUser(), CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING_LATER);
            Future<Boolean> future = CommonPoolUtil.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    boolean success = customerAccountManager.batchInitCustomerAccounts(serviceContext.getUser().getTenantId());
                    if (success) {
                        enableCustomerAccountResult.setEnableStatus(CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE.getValue());
                    } else {
                        enableCustomerAccountResult.setEnableStatus(CustomerAccountType.CustomerAccountEnableSwitchStatus.FAILED.getValue());
                    }
                    return success;
                }
            });
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                enableCustomerAccountResult.setEnableStatus(CustomerAccountType.CustomerAccountEnableSwitchStatus.OPENING_LATER.getValue());
                enableCustomerAccountResult.setMessage("由于客户数量较大，创建客户账户大概需要2小时，请先关闭窗口，待完成后系统将发送CRM通知");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return enableCustomerAccountResult;
    }

    private Set<String> checkDisplayName(String tenantId) {
        try {
            Set<String> existDisplayNames = Sets.newHashSet();
            List<String> existCustomerAccountApiNames = objectDescribeService.checkDisplayNameExist(tenantId, CustomerAccountConstants.DISPLAY_NAME, "CRM");
            existCustomerAccountApiNames.forEach(x -> {
                if (!CustomerAccountConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(CustomerAccountConstants.DISPLAY_NAME);
                }
            });
            List<String> existPrepayApiNames = objectDescribeService.checkDisplayNameExist(tenantId, PrepayDetailConstants.DISPLAY_NAME, "CRM");
            existPrepayApiNames.forEach(x -> {
                if (!PrepayDetailConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(PrepayDetailConstants.DISPLAY_NAME);
                }
            });
            List<String> existRebateIncomeApiNames = objectDescribeService.checkDisplayNameExist(tenantId, RebateIncomeDetailConstants.DISPLAY_NAME, "CRM");
            existRebateIncomeApiNames.forEach(x -> {
                if (!RebateIncomeDetailConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(RebateIncomeDetailConstants.DISPLAY_NAME);
                }
            });
            List<String> existRebateOutcomeApiNames = objectDescribeService.checkDisplayNameExist(tenantId, RebateOutcomeDetailConstants.DISPLAY_NAME, "CRM");
            existRebateOutcomeApiNames.forEach(x -> {
                if (!RebateOutcomeDetailConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(RebateOutcomeDetailConstants.DISPLAY_NAME);
                }
            });

            List<String> existRebateUseRuleApinames = objectDescribeService.checkDisplayNameExist(tenantId, RebateUseRuleConstants.DISPLAY_NAME, "CRM");
            existRebateUseRuleApinames.forEach(x -> {
                if (!RebateUseRuleConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(RebateUseRuleConstants.DISPLAY_NAME);
                }
            });
            log.debug("checkDisplayName tenantId:{},Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("checkDisplayName error,tenantId:{}", tenantId, e);
            throw new CustomerAccountBusinessException(() -> e.getErrorCode().getCode(), e.getMessage());
        }
    }

    @Override
    public CustomerAccountType.InvalidCustomerAccountResult invalidCustomerAccount(ServiceContext serviceContext, CustomerAccountType.InvalidCustomerAccountArg invalidCustomerAccountArg) {
        IObjectData customerAccountObjectData = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), invalidCustomerAccountArg.getCustomerId());

        String lifeStatus = invalidCustomerAccountArg.getLifeStatus();
        if (Objects.isNull(lifeStatus)) {
            throw new ValidateException("While Invalid prepay id is null");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("lifeStatus", lifeStatus);

        ObjectDataDocument resultObjectData = this.triggerInvalidAction(serviceContext, CustomerAccountConstants.API_NAME, customerAccountObjectData.getId(), null);
        CustomerAccountType.InvalidCustomerAccountResult invalidCustomerAccountResult = new CustomerAccountType.InvalidCustomerAccountResult();
        invalidCustomerAccountResult.setSuccess(Objects.nonNull(resultObjectData));
        return invalidCustomerAccountResult;
    }

    /**
     * 创建客户的时候，如果客户账户开关已经打开会调用该接口创建对应的客户账户<br>
     * @param serviceContext
     * @param createCustomerAccountArg
     * @return
     */
    @Override
    public CustomerAccountType.CreateCustomerAccountResult createCustomerAccount(ServiceContext serviceContext, CustomerAccountType.CreateCustomerAccountArg createCustomerAccountArg) {
        log.info("begin create CustomerAccount,for customerId:{}", createCustomerAccountArg.getCustomerId());
        Map<String, String> customerIdLifeStatusMap = Maps.newHashMap();
        customerIdLifeStatusMap.put(createCustomerAccountArg.getCustomerId(), createCustomerAccountArg.getLifeStatus());
        List<IObjectData> result = customerAccountManager.batchInitCustomerAccountDatas(serviceContext.getUser(), customerIdLifeStatusMap);
        log.debug("InitCustomerAccount,cutomerId:{}, Result:{}", createCustomerAccountArg.getCustomerId(), result);
        CustomerAccountType.CreateCustomerAccountResult createCustomerAccountResult = new CustomerAccountType.CreateCustomerAccountResult();
        createCustomerAccountResult.setCustomerAccountId(result.get(0).getId());
        createCustomerAccountResult.setCustomerId(createCustomerAccountArg.getCustomerId());
        return createCustomerAccountResult;
    }

    @Override
    public CustomerAccountType.BulkCreateCustomerAccountResult bulkCreateCustomerAccount(ServiceContext serviceContext, CustomerAccountType.BulkInitCustomerAccountArg bulkInitCustomerAccountArg) {
        CustomerAccountType.BulkCreateCustomerAccountResult bulkCreateCustomerAccountResult = new CustomerAccountType.BulkCreateCustomerAccountResult();
        List<String> customerIds = bulkInitCustomerAccountArg.getCustomerIds();
        if (CollectionUtils.isEmpty(customerIds)) {
            return bulkCreateCustomerAccountResult;
        }
        Map<String, String> customerIdLifeStatusMap = Maps.newHashMap();
        customerIds.forEach(customerId -> customerIdLifeStatusMap.put(customerId, bulkInitCustomerAccountArg.getLifeStatus()));
        customerAccountManager.batchInitCustomerAccountDatas(serviceContext.getUser(), customerIdLifeStatusMap);
        return bulkCreateCustomerAccountResult;
    }

    @Override
    public SfaBulkInvalidModel.Result bulkInvalidCustomerAccount(ServiceContext serviceContext, SfaBulkInvalidModel.Arg sfaBulkInvalidModelArg) {
        List<IObjectData> customerAccountObjectDataList = customerAccountManager.listCustomerAccountByCustomerIds(serviceContext.getUser(), sfaBulkInvalidModelArg.getDataIds());
        Map<String, IObjectData> customerAccountDataMap = customerAccountObjectDataList.stream().collect(Collectors.toMap(objectData -> ObjectDataUtil.getReferenceId(objectData, CustomerAccountConstants.Field.Customer.apiName), ob -> ob));
        BulkInvalidModel.Arg bulkInvalidModelArg = new BulkInvalidModel.Arg();
        for (String customerId : sfaBulkInvalidModelArg.getDataIds()) {
            BulkInvalidModel.InvalidArg invalidArg = new BulkInvalidModel.InvalidArg();
            // 获取客户账户id
            IObjectData customerAccountObjectData = customerAccountDataMap.get(customerId);//customerAccountManger.getCustomerAccountByCustomerId(serviceContext.getUser(), customerId);
            if (Objects.isNull(customerAccountObjectData)) {
                log.warn("CustomerId[{}]对应的客户账户不存在", customerId);
                continue;
            }
            invalidArg.setId(customerAccountObjectData.getId());
            invalidArg.setLifeStatus(sfaBulkInvalidModelArg.getLifeStatus());
            invalidArg.setObjectDescribeApiName(CustomerAccountConstants.API_NAME);
            if (null == bulkInvalidModelArg.getDataList()) {
                bulkInvalidModelArg.setDataList(new ArrayList<BulkInvalidModel.InvalidArg>());
            }
            bulkInvalidModelArg.getDataList().add(invalidArg);
        }

        StandardBulkInvalidAction.Arg standardBulkInvalidActionArg = new StandardBulkInvalidAction.Arg();
        standardBulkInvalidActionArg.setJson(JsonUtil.toJson(bulkInvalidModelArg));
        Map<String, Object> params = Maps.newHashMap();
        params.put("lifeStatus", sfaBulkInvalidModelArg.getLifeStatus());
        StandardBulkInvalidAction.Result result = this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.BulkInvalid.name(), standardBulkInvalidActionArg, params);
        return new SfaBulkInvalidModel.Result(result.getObjectDataList());
    }

    @Override
    public CustomerAccountType.EnableCreditResult updateCreditSwitch(ServiceContext serviceContext, CustomerAccountType.UpdateCreditSwitchArg updateCreditSwitch) {
        int switchType = updateCreditSwitch.getSwitchType();
        CustomerAccountConfig customerAccountConfig = customerAccountConfigManager.getConfigByTenantId(serviceContext.getTenantId());
        if (CustomerAccountType.CreditSwitchType.ClOSE.getValue() == switchType) {
            customerAccountConfig.setCreditEnable(false);
        } else if (CustomerAccountType.CreditSwitchType.OPEN.getValue() == switchType) {
            customerAccountConfig.setCreditEnable(true);
        } else {
            log.error("参数错误，for tenantId：{},EnableCreditArg：{}", serviceContext.getTenantId(), updateCreditSwitch);
            throw new ValidateException("打开/关闭信用：参数错误");
        }
        customerAccountConfigDao.update(customerAccountConfig);

        CustomerAccountType.EnableCreditResult enableCreditResult = new CustomerAccountType.EnableCreditResult();
        enableCreditResult.setEnableStatus(true);
        return enableCreditResult;
    }

    @Override
    public StandardBulkDeleteAction.Result bulkDeleteCustomerAccount(ServiceContext serviceContext, CustomerAccountType.BulkDeleteCustomerAccountArg deleteCustomerAccountArg) {
        Map<String, Object> params = Maps.newHashMap();
        List<IObjectData> customerObjectDatas = customerAccountManager.listCustomerAccountIncludeInvalidByCustomerIds(serviceContext.getUser(), deleteCustomerAccountArg.getCustomerIds());
        List<String> customerAccountIds = customerObjectDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
        StandardBulkDeleteAction.Arg arg = new StandardBulkDeleteAction.Arg();
        arg.setDescribeApiName(CustomerAccountConstants.API_NAME);
        arg.setIdList(customerAccountIds);
        return this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.BulkDelete.name(), arg, params);
    }

    @Override
    public EmptyResult unlockCustomerAccount(CustomerAccountType.UnlockCustomerAccountArg unlockCustomerAccountArg, ServiceContext serviceContext) {
        IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), unlockCustomerAccountArg.getCustomerId());
        BaseObjectLockAction.Arg arg = new BaseObjectLockAction.Arg();
        arg.setDataIds(Lists.newArrayList(customerAccountObj.getId()));
        this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.Unlock.name(), arg);
        return new EmptyResult();
    }

    @Override
    public EmptyResult lockCustomerAccount(CustomerAccountType.LockCustomerAccountArg lockCustomerAccountArg, ServiceContext serviceContext) {
        IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), lockCustomerAccountArg.getCustomerId());
        BaseObjectLockAction.Arg arg = new BaseObjectLockAction.Arg();
        arg.setDataIds(Lists.newArrayList(customerAccountObj.getId()));
        this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.Lock.name(), arg);
        return new EmptyResult();
    }

    @Override
    public Result flowComplete(Arg arg, ServiceContext serviceContext) {
        FlowCompleteModel.Result result = new FlowCompleteModel.Result();
        String lifeStatus = arg.getLifeStatus();
        String passStatus = RebateIncomeDetailFlowCompletedAction.Arg.PASS;
        String customerId = arg.getDataId();
        ApprovalFlowTriggerType approvalFlowTriggerType = null;
        if (ApprovalFlowTriggerType.CREATE.getId().equals(arg.getApprovalType())) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notPass";
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "pass";
            } else if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                result.setSuccess(true);
                log.info("nochange flowComplete, arg={}", arg);
                //新建客户驳回，然后编辑到这个分支了
                IObjectData objectData = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), customerId);
                String oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                if (!lifeStatus.equals(oldLifeStatus)) {
                    objectData.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                    serviceFacade.updateObjectData(serviceContext.getUser(), objectData);
                }
                return result;
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.CREATE;
        } else if (ApprovalFlowTriggerType.INVALID.getId().equals(arg.getApprovalType())) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notPass";
            } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                passStatus = "pass";
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "notPass";
            } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                result.setSuccess(true);
                log.info("nochange flowComplete, arg={}", arg);
                return result;
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.INVALID;
        } else if (ApprovalFlowTriggerType.UPDATE.getId().equals(arg.getApprovalType())) {
            if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "pass";
            } else {
                passStatus = "notPass";
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.UPDATE;
        } else {
            throw new ValidateException(String.format("ApprovalType[%s]无效", arg.getApprovalType()));
        }
        IObjectData objectData = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), customerId);
        StandardFlowCompletedAction.Arg arg1 = new StandardFlowCompletedAction.Arg();
        arg1.setDataId(objectData.getId());
        arg1.setDescribeApiName(objectData.getDescribeApiName());
        arg1.setTenantId(serviceContext.getTenantId());
        arg1.setUserId(serviceContext.getUser().getUserId());
        arg1.setTriggerType(approvalFlowTriggerType.getTriggerTypeCode());
        arg1.setStatus(passStatus);
        StandardFlowCompletedAction.Result flowResult = this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, "FlowCompleted", arg1);
        result.setSuccess(flowResult.getSuccess());
        return result;
    }

    @Override
    public EmptyResult bulkUnlockCustomerAccount(CustomerAccountType.BulkUnlockCustomerAccountArg bulkUnlockCustomerAccountArg, ServiceContext serviceContext) {
        List<String> customerAccountIdList = new ArrayList<>();
        for (String customerId : bulkUnlockCustomerAccountArg.getCustomerIds()) {
            IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), customerId);
            customerAccountIdList.add(customerAccountObj.getId());
        }

        BaseObjectLockAction.Arg arg = new BaseObjectLockAction.Arg();
        arg.setDataIds(customerAccountIdList);
        this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.Unlock.name(), arg);
        return new EmptyResult();
    }

    @Override
    public EmptyResult bulkLockCustomerAccount(CustomerAccountType.BulkLockCustomerAccountArg bulkLockCustomerAccountArg, ServiceContext serviceContext) {
        List<String> customerAccountIdList = new ArrayList<>();
        for (String customerId : bulkLockCustomerAccountArg.getCustomerIds()) {
            IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(serviceContext.getUser(), customerId);
            customerAccountIdList.add(customerAccountObj.getId());
        }

        BaseObjectLockAction.Arg arg = new BaseObjectLockAction.Arg();
        arg.setDataIds(customerAccountIdList);
        this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.Lock.name(), arg);
        return new EmptyResult();
    }

    @Override
    public CustomerAccountType.BulkRecoverCustomerAccountResult bulkRecover(ServiceContext serviceContext, CustomerAccountType.BulkRecoverCustomerAccountArg bulkRecoverCustomerAccountArg) {
        List<String> customerIds = bulkRecoverCustomerAccountArg.getCustomerIds();
        List<ObjectDataDocument> objectDataDocumentList = Lists.newArrayList();
        StandardBulkRecoverAction.Arg customerRecoverArg = new StandardBulkRecoverAction.Arg();
        customerRecoverArg.setObjectDescribeAPIName(CustomerAccountConstants.API_NAME);
        List<String> customerAccountIds = Lists.newArrayList();
        List<String> customerAccountIdsOfNotInvalidLifeStatus = Lists.newArrayList();
        for (String customerId : customerIds) {
            IObjectData customerAccountData = customerAccountManager.getDeletedObjByField(serviceContext.getUser(), CustomerAccountConstants.API_NAME, CustomerAccountConstants.Field.Customer.apiName, customerId);
            String lifeStatus = customerAccountData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                customerAccountIds.add(customerAccountData.getId());
            } else if (Sets.newHashSet(SystemConstants.LifeStatus.Ineffective.value, SystemConstants.LifeStatus.Normal.value).contains(lifeStatus)) {
                customerAccountIdsOfNotInvalidLifeStatus.add(customerAccountData.getId());
                objectDataDocumentList.add(ObjectDataDocument.of(customerAccountData));
            }
        }
        if (CollectionUtils.isNotEmpty(customerAccountIdsOfNotInvalidLifeStatus)) {
            log.warn("customerAccountIdsNotInvalidLifeStatus:{}", customerAccountIdsOfNotInvalidLifeStatus);
        }
        if (CollectionUtils.isNotEmpty(customerAccountIds)) {
            customerRecoverArg.setIdList(customerAccountIds);
            StandardBulkRecoverAction.Result result = this.triggerAction(serviceContext, CustomerAccountConstants.API_NAME, StandardAction.BulkRecover.name(), customerRecoverArg);
            if (result.getSuccess() && CollectionUtils.isNotEmpty(result.getDataList())) {
                objectDataDocumentList.addAll(result.getDataList());
            }
        }
        return new CustomerAccountType.BulkRecoverCustomerAccountResult(objectDataDocumentList);
    }

    @Override
    public CustomerAccountType.MergeCustomerResult merge(ServiceContext serviceContext, CustomerAccountType.MergeCustomerArg arg) {
        log.info("merge arg:{}", arg);
        List<String> allCustomerIds = Lists.newArrayList();
        allCustomerIds.addAll(arg.getSourceCustomerIds());
        allCustomerIds.add(arg.getMainCustomerId());
        List<IObjectData> customerAccountObjectDatas = customerAccountManager.listCustomerAccountIncludeInvalidByCustomerIds(serviceContext.getUser(), allCustomerIds);
        if (arg.getRelativeObjectMerge() == null || arg.getRelativeObjectMerge() == Boolean.FALSE) {
            CustomerAccountType.CanInvalidByCustomerIdsArg canInvalidByCustomerIdsArg = new CustomerAccountType.CanInvalidByCustomerIdsArg();
            canInvalidByCustomerIdsArg.setCustomerIds(arg.getSourceCustomerIds());
            CustomerAccountType.CanInvalidByCustomerIdsResult result = canInvalidByCustomerIds(serviceContext, canInvalidByCustomerIdsArg);
            if (!result.isSuccess()) {
                log.info("merge fail.errorReason:{}", result.getErrorReasons().toString());
                throw new ValidateException("客户账户余额不为零或者存在审批中的明细记录");
            } else {
                deleteCustomerAccounts(serviceContext, customerAccountObjectDatas, arg.getMainCustomerId());
                return new CustomerAccountType.MergeCustomerResult("0", "ok");
            }
        }
        log.info("merge customerAccountObjectDatas:{}", customerAccountObjectDatas);
        Map<String, IObjectData> customerIdMap = customerAccountObjectDatas.stream().collect(Collectors.toMap(ob -> ObjectDataUtil.getReferenceId(ob, CustomerAccountConstants.Field.Customer.apiName), o -> o));
        IObjectData mainObjectData = customerIdMap.get(arg.getMainCustomerId());
        String mainCustomerId = ObjectDataUtil.getReferenceId(mainObjectData, CustomerAccountConstants.Field.Customer.apiName);
        String mainCustomerAccountId = mainObjectData.getId();
        for (String customerId : arg.getSourceCustomerIds()) {
            ListByIdModel.Arg prepayArg = new ListByIdModel.Arg();
            prepayArg.setId(customerId);
            prepayArg.setPageSize(500);
            prepayArg.setPageNumber(1);
            ListByIdModel.RebateArg rebateArg = new ListByIdModel.RebateArg();
            rebateArg.setId(customerId);
            rebateArg.setPageSize(500);
            rebateArg.setPageNumber(1);
            List<ObjectDataDocument> prepayObjectDatas;
            List<ObjectDataDocument> rebateObjectDatas;
            do {
                prepayObjectDatas = prepayDetailService.listByCustomerId(prepayArg, serviceContext).getObjectDatas();
                log.info("merge prepayArg:{},prepayObjectDatas:{}", prepayArg, prepayObjectDatas);
                if (CollectionUtils.isNotEmpty(prepayObjectDatas)) {
                    for (ObjectDataDocument objectData : prepayObjectDatas) {
                        IObjectData objectData1 = objectData.toObjectData();
                        objectData1.set(PrepayDetailConstants.Field.Customer.apiName, mainCustomerId);
                        objectData1.set(PrepayDetailConstants.Field.CustomerAccount.apiName, mainCustomerAccountId);
                        serviceFacade.updateObjectData(serviceContext.getUser(), objectData1);
                    }
                }

            } while (CollectionUtils.isNotEmpty(prepayObjectDatas));

            do {
                rebateObjectDatas = rebateIncomeDetailService.listByCustomerId(rebateArg, serviceContext).getObjectDatas();
                log.info("merge rebateArg:{},rebateObjectDatas:{}", rebateArg, rebateObjectDatas);
                if (CollectionUtils.isNotEmpty(rebateObjectDatas)) {
                    for (ObjectDataDocument objectData : rebateObjectDatas) {
                        IObjectData objectData1 = objectData.toObjectData();
                        objectData1.set(RebateIncomeDetailConstants.Field.Customer.apiName, mainCustomerId);
                        objectData1.set(RebateIncomeDetailConstants.Field.CustomerAccount.apiName, mainCustomerAccountId);
                        serviceFacade.updateObjectData(serviceContext.getUser(), objectData1);
                    }
                }

            } while (CollectionUtils.isNotEmpty(rebateObjectDatas));
        }
        //账户金额放在后面合并，保证最终一致性就行
        mainObjectData = mergeCustomerAccountMoney(serviceContext, customerAccountObjectDatas, mainObjectData);

        //获取源数据
        List<String> sourceCustomerAccountIds = customerAccountObjectDatas.stream().filter(x -> arg.getSourceCustomerIds().contains(x.get(CustomerAccountConstants.Field.Customer.apiName, String.class))).map(x -> x.getId()).collect(Collectors.toList());
        customerAccountBillManager.mergeBill(sourceCustomerAccountIds, mainCustomerAccountId);

        deleteCustomerAccounts(serviceContext, customerAccountObjectDatas, mainCustomerId);
        return new CustomerAccountType.MergeCustomerResult("0", "ok");
    }

    private void deleteCustomerAccounts(ServiceContext serviceContext, List<IObjectData> customerAccountObjectDatas, String mainCustomerId) {
        Iterator iterator = customerAccountObjectDatas.iterator();
        while (iterator.hasNext()) {
            IObjectData objectData = (IObjectData) iterator.next();
            String customerID = ObjectDataUtil.getReferenceId(objectData, CustomerAccountConstants.Field.Customer.apiName);
            if (customerID.equals(mainCustomerId)) {
                iterator.remove();
                continue;
            }
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            /*客户在未生效，锁定，审批中都不可合并，只有正常状态和作废状态合并*/
            if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                log.info("merge invalid:{}", objectData);
                serviceFacade.bulkDelete(Lists.newArrayList(objectData), serviceContext.getUser());
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                log.info("merge normal:{}", objectData);
                IObjectData invalidData = serviceFacade.invalid(objectData, serviceContext.getUser());
                serviceFacade.bulkDelete(Lists.newArrayList(invalidData), serviceContext.getUser());
            }
        }
    }

    private IObjectData mergeCustomerAccountMoney(ServiceContext serviceContext, List<IObjectData> customerAccountObjectDatas, IObjectData mainObjectData) {
        BigDecimal prepayBalance = new BigDecimal(0);
        BigDecimal prepayAvail = new BigDecimal(0);
        BigDecimal prepayLock = new BigDecimal(0);
        BigDecimal rebateAvail = new BigDecimal(0);
        BigDecimal rebateLock = new BigDecimal(0);
        BigDecimal rebateBalance = new BigDecimal(0);
        BigDecimal credit = new BigDecimal(0);
        for (IObjectData objectData : customerAccountObjectDatas) {
            prepayBalance = prepayBalance.add(objectData.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class));
            prepayAvail = prepayAvail.add(objectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class));
            prepayLock = prepayLock.add(objectData.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class));
            rebateBalance = rebateBalance.add(objectData.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class));
            rebateAvail = rebateAvail.add(objectData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class));
            rebateLock = rebateLock.add(objectData.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class));
            credit = credit.add(objectData.get(CustomerAccountConstants.Field.CreditQuota.apiName, BigDecimal.class));
        }
        mainObjectData.set(CustomerAccountConstants.Field.PrepayBalance.apiName, prepayBalance);
        mainObjectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, prepayLock);
        mainObjectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, prepayAvail);
        mainObjectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, rebateAvail);
        mainObjectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, rebateLock);
        mainObjectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, rebateBalance);
        mainObjectData.set(CustomerAccountConstants.Field.CreditQuota.apiName, credit);
        log.info("merge mainObjectData:{}", mainObjectData);
        IObjectData mainData = serviceFacade.updateObjectData(serviceContext.getUser(), mainObjectData);
        return mainData;
    }
}
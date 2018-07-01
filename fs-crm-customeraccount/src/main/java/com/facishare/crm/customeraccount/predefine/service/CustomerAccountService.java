package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.predefine.service.dto.FlowCompleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;

/**
 * 客户账户相关操作<br>
 * Created by xujf on 2017/9/26.
 */
@ServiceModule("customer_account")
public interface CustomerAccountService {

    @ServiceMethod("flow_complete")
    FlowCompleteModel.Result flowComplete(FlowCompleteModel.Arg arg, ServiceContext serviceContext);

    /**
     *
     * @param serviceContext
     * @return
     */
    @ServiceMethod("is_customer_account_enable")
    CustomerAccountType.IsCustomerAccountEnableResult isCustomerAccountEnable(ServiceContext serviceContext);

    /**
     * 1.新建销售订单的时候，需要展示可用预存款/返利余额/可用信用<br>
     * 2.下单的时候需要校验 客户账户余额（预存款+返利）+ 信用/客户账户余额 是否足够下单。
     * @param serviceContext
     * @param getCustomerAccountAndCreditInfoArg
     * @return
     */
    @ServiceMethod("get_customer_account_and_credit_info")
    CustomerAccountType.GetCustomerAccountAndCreditInfoResult getCustomerAccountAndCreditInfo(ServiceContext serviceContext, CustomerAccountType.GetCustomerAccountAndCreditInfoArg getCustomerAccountAndCreditInfoArg);

    /**
     * 信用+可用余额>订单金额则该订单可以创建<br>
     * 否则不可以创建订单<br>
     * 使用场景：新建订单，
     * a).选择先付结算方式，需要根据余额 +信用 来判断订单是否创建。
     * b).选择后付：如果启用了信用则只校验信用。<br>
     *
     * @return
     */
    @ServiceMethod("is_balance_credit_enough")
    CustomerAccountType.BalanceCreditEnoughResult isBalanceAndCreditEnough(ServiceContext serviceContext, CustomerAccountType.OrderArg arg);

    /**
     * 根据客户ID查询客户账户对象<br>
     * 使用场景：a).客户详情 页面 展示客户账户信息<br>
     *
     * @return
     */
    @ServiceMethod("get_by_customer_id")
    CustomerAccountType.GetByCustomerIdResult getByCustomerId(ServiceContext serviceContext, CustomerAccountType.GetByCustomerIdArg customerId);

    /**
     * 新建回款选择支付方式为 预存款/返利/预存款+返利 ，需要判断余额是否够。
     * 如果用选择预存款+返利，则分别填入预存款数字和返利数字，所以需要分别校验预存款、返利是否足够。<br>
     *
     * @param arg
     * @return
     */
    @ServiceMethod("is_balance_enough")
    CustomerAccountType.BalanceEnoughResult isBalanceEnough(ServiceContext serviceContext, CustomerAccountType.PaymentArg arg);

    /**
     * 是否可以批量作废客户<br>
     * 使用场景：客户页面点击作废按钮。<br>
     * 客户作废时，需要校验客户账户余额是否为零，如果是零，可作废，否则不可作废。<br>
     * (注意：与信用没有关系)。
     * @return
     */
    @ServiceMethod("can_invalid_by_customerids")
    CustomerAccountType.CanInvalidByCustomerIdsResult canInvalidByCustomerIds(ServiceContext serviceContext, CustomerAccountType.CanInvalidByCustomerIdsArg canInvalidByCustomerIdsArg);

    /**
     * 查询客户信用是否开启<br>
     * 使用场景：创建订单（包括代客下单、订货通H5下单），需要校验是否开启信用。<br>
     */
    @ServiceMethod("is_credit_enable")
    CustomerAccountType.IsCreditEnableResult isCreditEnable(ServiceContext serviceContext);

    /**
     * 根据客户Id查询客户信用<br>
     * 应用场景：新建销售订单，下游h5下单需要 获取该客户的信用，信用=初始信用-未回款订单金额。该接口需要调用北京的接口获取未回款订单总金额。<br>
     *
     * @param customerId
     */
    @ServiceMethod("get_available_credit")
    CustomerAccountType.GetAvailableCreditResult getAvailableCredit(ServiceContext serviceContext, CustomerAccountType.GetAvailableCreditArg customerId);

    @ServiceMethod("update_credit_switch")
    CustomerAccountType.EnableCreditResult updateCreditSwitch(ServiceContext serviceContext, CustomerAccountType.UpdateCreditSwitchArg updateCreditSwitch);

    /**
      * 启用客户账户<br>
      * 1.给3个自定义对象初始化描述 <br>
      * 2.给企业里的每个客户初始化客户账户数据<br>
      * @param serviceContext
      * @return
      */
    @ServiceMethod("enable_customer_account")
    CustomerAccountType.EnableCustomerAccountResult enableCustomerAccount(ServiceContext serviceContext);

    /**
     * 使用场景：<br>
     * 1.页面打开“客户账户开启开关”会先调用enableCustomerAccount()初始化描述，此时如果客户数目大于阈值则通过定时任务调用该接口 创建客户账户，如果小于则立马调改接口 创建客户账户数据。<br>
     * 2.创建客户时候，判断客户账户开关是否开启，如果开发则调用该接口初始化客户账户数据。<br>
     * @param serviceContext
     * @param createCustomerAccountArg
     * @return
     */
    @ServiceMethod("create")
    CustomerAccountType.CreateCustomerAccountResult createCustomerAccount(ServiceContext serviceContext, CustomerAccountType.CreateCustomerAccountArg createCustomerAccountArg);

    @ServiceMethod("bulk_create")
    CustomerAccountType.BulkCreateCustomerAccountResult bulkCreateCustomerAccount(ServiceContext serviceContext, CustomerAccountType.BulkInitCustomerAccountArg bulkInitCustomerAccountArg);

    @ServiceMethod("bulk_delete")
    StandardBulkDeleteAction.Result bulkDeleteCustomerAccount(ServiceContext serviceContext, CustomerAccountType.BulkDeleteCustomerAccountArg accountArg);

    @ServiceMethod("invalid")
    CustomerAccountType.InvalidCustomerAccountResult invalidCustomerAccount(ServiceContext serviceContext, CustomerAccountType.InvalidCustomerAccountArg invalidCustomerAccountArg);

    @ServiceMethod("bulk_invalid")
    SfaBulkInvalidModel.Result bulkInvalidCustomerAccount(ServiceContext serviceContext, SfaBulkInvalidModel.Arg sfaBulkInvalidModelArg);

    @ServiceMethod("unlock")
    EmptyResult unlockCustomerAccount(CustomerAccountType.UnlockCustomerAccountArg arg, ServiceContext serviceContext);

    @ServiceMethod("lock")
    EmptyResult lockCustomerAccount(CustomerAccountType.LockCustomerAccountArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_unlock")
    EmptyResult bulkUnlockCustomerAccount(CustomerAccountType.BulkUnlockCustomerAccountArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_lock")
    EmptyResult bulkLockCustomerAccount(CustomerAccountType.BulkLockCustomerAccountArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_recover")
    CustomerAccountType.BulkRecoverCustomerAccountResult bulkRecover(ServiceContext serviceContext, CustomerAccountType.BulkRecoverCustomerAccountArg bulkRecoverCustomerAccountArg);

    @ServiceMethod("merge")
    CustomerAccountType.MergeCustomerResult merge(ServiceContext serviceContext, CustomerAccountType.MergeCustomerArg arg);

}

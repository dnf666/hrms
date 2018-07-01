package com.facishare.crm.customeraccount.constants;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by xujf on 2018/2/26.
 */
public class ImportConstants {

    public static final List<String> REBATE_INCOME_MUST_NOT_EXPORT_HEADER_FILTER = Lists.newArrayList(SystemConstants.Field.ExtendObjDataId.apiName, RebateIncomeDetailConstants.Field.Refund.apiName, RebateIncomeDetailConstants.Field.Attach.apiName, RebateIncomeDetailConstants.Field.AvailableRebate.apiName, RebateIncomeDetailConstants.Field.UsedRebate.apiName);

    public static final List<String> PREPAY_MUST_NOT_EXPORT_HEADER_FILTER = Lists.newArrayList(SystemConstants.Field.ExtendObjDataId.apiName, PrepayDetailConstants.Field.OutcomeType.apiName, PrepayDetailConstants.Field.OrderPayment.apiName, PrepayDetailConstants.Field.Refund.apiName, PrepayDetailConstants.Field.OnlineChargeNo.apiName);

    public static final List<String> CUSTOMER_ACCOUNT_EXPORT_HEADER_FILTER = Lists.newArrayList(CustomerAccountConstants.Field.Name.apiName, CustomerAccountConstants.Field.CreditQuota.apiName, CustomerAccountConstants.Field.SettleType.apiName, CustomerAccountConstants.Field.Customer.apiName);

    public static final String MULTI_VALUE_SEPARATOR = "|";

    public static final String DEFAULT_RECORD_TYPE_VALUE = "default__c";//默认业务类型
}

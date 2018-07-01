package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.CurlModel;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.predefine.service.impl.CurlServiceImpl;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("customer_account_curl")
public interface CurlService {
    @ServiceMethod("enable_customer_account")
    EmptyResult enableCustomerAccountByCurl(CurlModel.TenantIds arg, ServiceContext serviceContext);

    @ServiceMethod("query_plain_customer")
    CurlModel.QueryCustomerResult queryCustomersFromPg(CurlModel.QueryCustomerArg arg, ServiceContext serviceContext);

    @ServiceMethod("fix_customer_account_status")
    CurlModel.FixCustomerAccountLifeStatusResult fixCustomerAccountLifeStatus(CurlModel.FixCustomerAccountLifeStatusArg arg, ServiceContext serviceContext);

    @ServiceMethod("fix_prepay_layout_record_Type")
    EmptyResult initPrepayLayoutRecordType(ServiceContext serviceContext);

    @ServiceMethod("fix_init_approval")
    EmptyResult initApproval(CurlModel.ObjectApiNameArg arg, ServiceContext serviceContext);

    @ServiceMethod("fix_in_out_come_select_one")
    EmptyResult updateSelectOneFieldDescribe(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext);

    @ServiceMethod("fix_select_one_field")
    CurlModel.FixSelectOneFieldResult fixSelectOneFieldDescribe(CurlModel.FixSelectOneFieldArg arg, ServiceContext serviceContext);

    @ServiceMethod("update_agent_type")
    EmptyResult updateLayout(CurlModel.UpdateLayoutArg arg, ServiceContext serviceContext);

    @ServiceMethod("list_customer_status_before_invalid")
    CurlModel.CustomerStatusBeforeInvalidResult listCustomerStatusBeforeInvalid(CurlModel.CustomerStatusBeforeInvalidArg arg, ServiceContext serviceContext);

    @ServiceMethod("init_lacked_customer_account_data")
    CurlModel.LackCustomerAccountInitResult initLackedCustomerAccountData(CurlModel.TenantIds arg, ServiceContext serviceContext);

    @ServiceMethod("tenantIds_lack_customer_account_data")
    CurlModel.TenantIds listTenantIdsOfLackCustomerAccountDatas(CurlModel.TenantIds arg, ServiceContext serviceContext);

    @ServiceMethod("add_order_payment_field")
    CurlModel.AddOrderPaymentFieldResult addOrderPaymentField(CurlModel.AddOrderPaymentFieldArg arg, ServiceContext serviceContext);

    @ServiceMethod("add_payment_field")
    CurlModel.AddOrderPaymentFieldResult addPaymentField(CurlModel.AddOrderPaymentFieldArg arg, ServiceContext serviceContext);

    @ServiceMethod("del_payment_field")
    CurlModel.DelPaymentFieldResult delPaymentField(ServiceContext serviceContext);

    @ServiceMethod("add_import_functin_privilege_to_role")
    CurlModel.AddImportPrivilegeResult addImportFunctionPrivilegeToRole(CurlModel.AddImportPrivilegeArg arg);

    @ServiceMethod("del_import_functin_privilege")
    CurlModel.AddImportPrivilegeResult delImportFunctionPrivilege(CurlModel.DelImportPrivilegeArg arg);

    @ServiceMethod("del_import_functin_privilege_to_role")
    CurlModel.AddImportPrivilegeResult delImportFunctionPrivilegeToRole(CurlModel.AddImportPrivilegeArg arg1);

    @ServiceMethod("fix_rebate_income_list_layout")
    CurlModel.ListLayoutResult fixRebateIncomeListLayout(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext);

    @ServiceMethod("fix_rebate_income_label_and_transaction_time")
    EmptyResult fixRebateIncomeStartEndTimeLabelAndTransactionTime(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext);

    @ServiceMethod("init_rebate_use_rule")
    CurlServiceImpl.TenantIdModel.Result initRebateUseRule(CurlServiceImpl.TenantIdModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("add_sales_order_and_rebate_use_rule_field")
    CurlServiceImpl.TenantIdModel.Result addSalesOrderAndRebateUseRuleField(CurlServiceImpl.TenantIdModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("fix_rebate_amount_by_rebate_income_type_null")
    EmptyResult fixRebateAmountByRebateIncomeTypeNull(CurlModel.RebateIncomeIdArg arg, ServiceContext serviceContext);

    @ServiceMethod("fix_customer_account_relate_balance")
    EmptyResult fixCustomerAccountRelateBalance(CurlModel.FixCustomerAccountBalanceArg arg, ServiceContext serviceContext);

    @ServiceMethod("find_object_describe_by_api_name")
    CurlModel.FixSelectOneFieldResult findDescribeByApiName(CurlModel.FixSelectOneFieldArg arg, ServiceContext serviceContext);

    @ServiceMethod("addSelectOptionInRebateIncomeType")
    CurlServiceImpl.TenantIdModel.Result addSelectOptionInRebateIncomeType(CurlServiceImpl.TenantIdModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("addRebateUseRuleFieldInRebateOutcome")
    CurlServiceImpl.TenantIdModel.Result addRebateUseRuleFieldInRebateOutcome(CurlServiceImpl.TenantIdModel.Arg arg, ServiceContext serviceContext);
}
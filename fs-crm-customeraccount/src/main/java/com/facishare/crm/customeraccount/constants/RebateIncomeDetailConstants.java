package com.facishare.crm.customeraccount.constants;

public interface RebateIncomeDetailConstants {
    String API_NAME = "RebateIncomeDetailObj";
    String DISPLAY_NAME = "返利";
    String DEFAULT_LAYOUT_API_NAME = "RebateIncomeDetailObj_default_layout__c";
    String DEFUALT_LAYOUT_DISPLAY_NAME = "默认布局";

    String LIST_LAYOUT_API_NAME = "RebateIncomeDetailObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "rebate_income_detail";

    String START_END_TIME_RULE_API_NAME = "start_end_time_rule__c";
    String START_END_TIME_RULE_DISPLAY_NAME = "日期校验";
    String START_END_TIME_RULE_DESCRIPTION = "开始日期不能大于结束日期";

    int ICON_INDEX = 13;
    String LIFE_STATUS_HELP_TEXT = "由退款关联产生的返利记录，不走审批流，它的状态随退款的状态变化而变化。";

    enum Field {
        Name("name", "返利单号"),

        Customer("customer_id", "客户名称", "target_related_list_rebate_income_customer", "返利"),

        CustomerAccount("customer_account_id", "客户账户", "target_related_list_rebate_income_customer_account", "返利"),

        Amount("amount", "返利金额(元)"),

        TransactionTime("transaction_time", "交易时间"),

        StartTime("start_time", "开始时间"),

        EndTime("end_time", "结束时间"),

        UsedRebate("used_rebate", "已用返利(元)"),

        AvailableRebate("available_rebate", "可用返利(元)"),

        //6.3
        SalesOrder("order_id", "订单编号", "target_related_list_rebate_income_order", "返利"),

        Refund("refund_id", "退款", "target_related_list_rebate_income_refund", "返利"),

        IncomeType("income_type", "收入类型"),

        Attach("attach", "附件"),

        Remark("remark", "备注"),;

        public String apiName;
        public String label;
        public String targetRelatedListName;
        public String targetRelatedListLabel;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        Field(String apiName, String label, String targetRelatedListName, String targetRelatedListLabel) {
            this.apiName = apiName;
            this.label = label;
            this.targetRelatedListName = targetRelatedListName;
            this.targetRelatedListLabel = targetRelatedListLabel;
        }
    }
}

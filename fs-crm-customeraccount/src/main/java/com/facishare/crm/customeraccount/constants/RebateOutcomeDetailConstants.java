package com.facishare.crm.customeraccount.constants;

public interface RebateOutcomeDetailConstants {
    String API_NAME = "RebateOutcomeDetailObj";
    String DISPLAY_NAME = "返利支出";
    String DEFAULT_LAYOUT_API_NAME = "RebateOutcomeDetailObj_default_layout__c";
    String DEFUALT_LAYOUT_DISPLAY_NAME = "默认布局";

    String LIST_LAYOUT_API_NAME = "RebateOutcomeDetailObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "rebate_outcome_detail";

    String LIFE_STATUS_HELP_TEXT = "由回款关联产生的返利记录，不走审批流，它的状态随回款的状态变化而变化。";

    enum Field {
        Name("name", "返利支出单号"),

        Customer("customer_id", "客户名称"),

        RebateIncomeDetail("rebate_income_detail_id", "返利收入", "target_related_list_rebate_outcome_income", "支出明细"),

        Amount("amount", "金额(元)"),

        TransactionTime("transaction_time", "交易时间"),

        Payment("payment_id", "回款编号", "target_related_list_rebate_outcome_payment", "返利支出"),

        OrderPayment("order_payment_id", "回款明细编号", "target_related_list_rebate_outcome_order_payment", "返利支出"),

        //6.3 返利使用规则
        RebateUseRule("rebate_use_rule_id", "使用规则", "target_related_list_rebate_outcome_rebate_use_rule", "返利支出"),

        ;
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

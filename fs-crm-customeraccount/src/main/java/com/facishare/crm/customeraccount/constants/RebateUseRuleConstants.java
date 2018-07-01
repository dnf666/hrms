package com.facishare.crm.customeraccount.constants;

public interface RebateUseRuleConstants {
    String API_NAME = "RebateUseRuleObj";
    String DISPLAY_NAME = "返利使用规则";
    String DEFAULT_LAYOUT_API_NAME = "RebateUseRule_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "RebateUseRule_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "rebate_use_rule";
    int ICON_INDEX = 21;

    String CUSTOMER_RANGE_SECTION_API_NAME = "customer_range_section__c";
    String customerRangeDefaultValue = "{\"type\":\"noCondition\",\"value\":\"ALL\"}";
    String expressionType = "long_text";

    String START_END_TIME_RULE_API_NAME = "start_end_time_rule__c";
    String START_END_TIME_RULE_DISPLAY_NAME = "日期校验";
    String START_END_TIME_RULE_DESCRIPTION = "开始日期不能大于结束日期";

    String usedMaxAmountHelpText = "订单回款中可使用返利的最高金额";
    String usedMaxPrecentHelpText = "通过订单金额*比例，算出订单回款中可使用返利的最高金额";

    enum Field {
        Name("name", "规则名称"),

        StartTime("start_time", "开始日期"),

        EndTime("end_time", "结束日期"),

        Status("status", "启用状态"),

        MinOrderAmount("min_order_amount", "最低订单金额"),

        UsedMaxAmount("used_max_amount", "可使用返利最高金额"),

        UsedMaxPrecent("used_max_precent", "可使用返利最高比例"),

        Remark("remark", "备注"),

        CustomerRange("customer_range", "适用客户"),;

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

        public String apiName;
        public String label;
        public String targetRelatedListName;
        public String targetRelatedListLabel;
    }

}

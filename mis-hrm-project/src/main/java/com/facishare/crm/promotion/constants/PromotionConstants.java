package com.facishare.crm.promotion.constants;

public interface PromotionConstants {
    String API_NAME = "PromotionObj";
    String DISPLAY_NAME = "促销";
    String DEFAULT_LAYOUT_API_NAME = "Promotion_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "Promotion_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "promotion";
    int ICON_INDEX = 20;

    String CUSTOMER_RANGE_SECTION_API_NAME = "customer_range_section__c";

    String TYPE_SECTION_API_NAME = "promotion_rule_section__c";

    String START_END_TIME_RULE_API_NAME = "start_end_time_rule__c";
    String START_END_TIME_RULE_DISPLAY_NAME = "日期校验";
    String START_END_TIME_RULE_DESCRIPTION = "开始日期不能大于结束日期";

    String customerRangeDefaultValue = "{\"type\":\"noCondition\",\"value\":\"ALL\"}";
    String expressionType = "long_text";

    enum Field {

        Name("name", "促销活动"),

        StartTime("start_time", "开始日期"),

        EndTime("end_time", "结束日期"),

        Images("images", "活动图片"),

        Status("status", "是否启用"),

        Type("type", "促销规则"),

        CustomerRange("customer_range", "适用客户"),

        ;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String apiName;
        public String label;
    }
}

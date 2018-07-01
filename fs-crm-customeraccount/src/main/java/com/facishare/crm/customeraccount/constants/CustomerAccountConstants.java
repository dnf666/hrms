package com.facishare.crm.customeraccount.constants;

public interface CustomerAccountConstants {
    String API_NAME = "CustomerAccountObj";
    String DISPLAY_NAME = "客户账户";
    String DETAIL_LAYOUT_API_NAME = "CustomerAccountObj_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "CustomerAccountObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "customer_account";
    int ICON_INDEX = 15;
    String LIFE_STATUS_HELP_TEXT = "客户账户由客户关联产生，无独立的审批流，它的状态随客户的状态变化而变化。";

    enum Field {

        Name("name", "账户ID"),

        Customer("customer_id", "客户名称", "target_related_list_customer_account_customer", "账户信息"), //target_related_list_customer__c

        PrepayBalance("prepay_balance", "预存款余额(元)"),

        PrepayAvailableBalance("prepay_available_balance", "预存款可用余额(元)"),

        PrepayLockedBalance("prepay_locked_balance", "预存款锁定余额(元)"),

        RebateBalance("rebate_balance", "返利余额(元)"),

        RebateAvailableBalance("rebate_available_balance", "返利可用余额(元)"),

        RebateLockedBalance("rebate_locked_balance", "返利锁定余额(元)"),

        SettleType("settle_type", "结算方式"),

        CreditQuota("credit_quota", "信用额度(元)");

        public String apiName;
        public String label;
        public String targetRelatedListName;
        public String targetRelatedListLabel;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        Field(String apiName, String label, String targetRelatedListName, String targetRelatedListLabel) {
            this.apiName = apiName;
            this.label = label;
            this.targetRelatedListName = targetRelatedListName;
            this.targetRelatedListLabel = targetRelatedListLabel;
        }
    }
}

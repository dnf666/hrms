package com.facishare.crm.sfa.utilities.constant;

public interface CustomerAccountConstants {
    String API_NAME = "CustomerAccountObj";
    String DISPLAY_NAME = "客户账户";
    String LAYOUT_API_NAME = API_NAME + "_LAYOUT";
    String LAYOUT_DISPLAY_NAME = DISPLAY_NAME + "Layout";

    enum Field {

        Name("name", "账户ID"),

        Customer("customer_id", "客户", "target_related_list_customer__c", "客户账户"),

        PrepayBalance("prepay_balance", "预存款余额"),

        PrepayAvailableBalance("prepay_available_balance", "预存款可用余额"),

        PrepayLockedBalance("prepay_locked_balance", "预存款锁定余额"),

        RebateBalance("rebate_balance", "返利余额"),

        RebateAvailableBalance("rebate_available_balance", "返利可用余额"),

        RebateLockedBalance("rebate_locked_balance", "返利锁定余额"),

        SettleType("settle_type", "结算方式"),

        CreditQuota("credit_quota", "信用额度");

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

    enum PayType {

        PREPAY("prepay", "先付"), POSTPAY("postpay", "后付");
        public String type;
        public String description;

        PayType(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}

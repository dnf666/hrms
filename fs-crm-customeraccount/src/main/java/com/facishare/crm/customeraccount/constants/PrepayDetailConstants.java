package com.facishare.crm.customeraccount.constants;

public interface PrepayDetailConstants {
    String API_NAME = "PrepayDetailObj";
    String DISPLAY_NAME = "预存款";

    String DEFAULT_LAYOUT_API_NAME = "PrepayDetailObj_default_layout__c";
    String DEFUALT_LAYOUT_DISPLAY_NAME = "默认布局";
    String INCOME_LAYOUT_API_NAME = "PrepayDetailObj_income_layout__c";
    String INCOME_LAYOUT_DISPLAY_NAME = "预存款收入布局";
    String OUTCOME_LAYOUT_API_NAME = "PrepayDetailObj_outcome_layout__c";
    String OUTCOME_LAYOUT_DISPLAY_NAME = "预存款支出布局";

    String LIST_LAYOUT_API_NAME = "PrepayDetailObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "prepay_detail";
    int ICON_INDEX = 14;
    String LIFE_STATUS_HELP_TEXT = "由回款/退款关联产生的预存款记录，不走审批流，它的状态随回款/退款的状态变化而变化。";

    enum Field {
        Name("name", "明细编码"),

        Customer("customer_id", "客户名称", "target_related_list_prepay_customer", "预存款"),

        CustomerAccount("customer_account_id", "客户账户", "target_related_list_prepay_customer_account", "预存款"),

        Amount("amount", "金额(元)"),

        TransactionTime("transaction_time", "交易时间"),

        Payment("payment_id", "回款编号", "target_related_list_prepay_payment", "预存款"),

        OrderPayment("order_payment_id", "回款明细编号", "target_related_list_prepay_order_payment", "预存款"),

        Refund("refund_id", "退款编号", "target_related_list_prepay_refund", "预存款"),

        IncomeType("income_type", "收入类型"),

        OutcomeType("outcome_type", "支出类型"),

        OnlineChargeNo("online_charge_no", "支付流水号"),

        Attach("attach", "附件"),

        Remark("remark", "备注"),

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

    enum RecordType {
        OutcomeRecordType("default__c", "支出"),

        IncomeRecordType("income_record_type__c", "收入"),

        ;

        public String apiName;
        public String label;

        RecordType(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }
    }
}

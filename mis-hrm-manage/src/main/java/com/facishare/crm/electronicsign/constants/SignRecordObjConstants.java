package com.facishare.crm.electronicsign.constants;

public interface SignRecordObjConstants {
    String API_NAME = "SignRecordObj";
    String DISPLAY_NAME = "签署记录";
    String DEFAULT_LAYOUT_API_NAME = "SignRecordObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "SignRecordObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "sign_record";
    int ICON_INDEX = 19; // TODO chenzs 用多少

    enum Field {
        Id("_id", "id"),
        Name("name", "签署记录编号"),

        QuotaType("quota_type", "配额类型"),
        AppType("app_type", "应用类型"),

        Origin("origin", "来源"),
        SalesOrderId("sales_order_id", "销售订单", "target_related_list_sr_sales_order_id", "签署记录"),
        AccountStatementId("account_statement_id", "对账单", "target_related_list_sr_account_statement_id", "签署记录"),
        DeliveryNoteId("delivery_note_id", "发货单", "target_related_list_sr_delivery_note_id", "签署记录"),

        ContractId("contract_id", "合同ID"),
        ContractFileAttachment("contract_file_attachment", "合同附件"),
        ;

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

    enum RecordType {
        DefaultRecordType("default__c", "预设业务类型"),
        ;

        public String apiName;
        public String label;

        RecordType(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }
    }
}
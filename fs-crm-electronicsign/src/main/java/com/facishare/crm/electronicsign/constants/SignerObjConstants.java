package com.facishare.crm.electronicsign.constants;

public interface SignerObjConstants {
    String API_NAME = "SignerObj";
    String DISPLAY_NAME = "签署方";
    String DEFAULT_LAYOUT_API_NAME = "SignerObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "SignerObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "signer";
    int ICON_INDEX = 19; // TODO chenzs 用多少

    enum Field {
        Name("name", "签署方编号"),
        SignRecordId("sign_record_id", "签署记录", "target_related_list_sr_sign_record_id", "签署方"),

        AccountSignCertifyId("account_sign_certify_id", "客户账户", "target_related_list_sr_account_sign_certify_id", "签署方"),
        InternalSignCertifyId("internal_sign_certify_id", "内部账户", "target_related_list_sr_internal_sign_certify_id", "签署方"),   //sr：signer的缩写，超过50个了
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
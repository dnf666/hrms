package com.facishare.crm.electronicsign.constants;

public interface InternalSignCertifyObjConstants {
    String API_NAME = "InternalSignCertifyObj";
    String DISPLAY_NAME = "内部签章认证";

    String DEFAULT_LAYOUT_API_NAME = "InternalSignCertifyObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "InternalSignCertifyObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "internal_sign_certify";
    int ICON_INDEX = 19; // TODO chenzs 用多少

    String CERTIFY_FIELD_SECTION = "认证资料";
    String CERTIFY_FIELD_SECTION_API_NAME = "certify_section__c";
    String USE_RANGE_FIELD_SECTION = "使用范围";
    String USE_RANGE_FIELD_SECTION_API_NAME = "use_range_section__c";

    enum Field {
        Id("_id", "id"),
        Name("name", "内部签章认证编号"),
        
        RegMobile("reg_mobile", "注册手机号"),

        //企业客户的信息
        EnterpriseName("enterprise_name", "企业名称"),
        UnifiedSocialCreditIdentifier("unified_social_credit_identifier", "统一社会信用代码"),
        LegalPersonName("legal_person_name", "法人或经办人姓名"),
        LegalPersonMobile("legal_person_mobile", "法人或经办人手机号"),
        LegalPersonIdentity("legal_person_identity", "法人或经办人身份证号"),

        BestSignAccount("best_sign_account", "实名认证账户"),
        CertId("cert_id", "证书id"),
        CertifyStatus("certify_status", "认证状态"),
        CertifyErrMsg("certify_err_msg", "错误信息"),

        UseStatus("use_status", "使用状态"),
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

    enum Button {
        Enable("Enable", "启用"),
        Disable("Disable", "停用"),
        ;

        public String apiName;
        public String label;

        Button(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
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
package com.facishare.crm.electronicsign.constants;

public interface AccountSignCertifyObjConstants {
    String API_NAME = "AccountSignCertifyObj";
    String DISPLAY_NAME = "客户签章认证";

    String DEFAULT_LAYOUT_API_NAME = "AccountSignCertifyObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String ENTERPRISE_LAYOUT_API_NAME = "AccountSignCertifyObj_enterprise_layout__c";
    String ENTERPRISE_LAYOUT_DISPLAY_NAME = "企业用户布局";  //在自定义对象管理的布局页可以看到
    String INDIVIDUAL_LAYOUT_API_NAME = "AccountSignCertifyObj_individual_layout__c";
    String INDIVIDUAL_LAYOUT_DISPLAY_NAME = "个人用户布局";

    String LIST_LAYOUT_API_NAME = "AccountSignCertifyObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "account_sign_certify";
    int ICON_INDEX = 19; // TODO chenzs 用多少

    enum Field {
        Id("_id", "id"),
        Name("name", "客户签章认证编号"),

        CertifyStatus("certify_status", "认证状态"),
        CertifyErrMsg("certify_err_msg", "错误信息"),
        BestSignAccount("best_sign_account", "实名认证账户"),
        RegMobile("reg_mobile", "注册手机号"),
        CertId("cert_id", "证书id"),
        AccountId("account_id", "客户", "target_related_list_isc_account_id", "客户签章认证"),  //isc：internal_sign_certify，以前发货单试过长度有限制，所以这里用缩写

        //企业客户的信息
        EnterpriseName("enterprise_name", "企业名称"),
        UnifiedSocialCreditIdentifier("unified_social_credit_identifier", "统一社会信用代码"),
        LegalPersonName("legal_person_name", "法人或经办人姓名"),
        LegalPersonIdentity("legal_person_identity", "法人或经办人身份证号"),
        LegalPersonMobile("legal_person_mobile", "法人或经办人手机号"),

        //个体客户的信息
        UserName("user_name", "姓名"),
        UserIdentity("user_identity", "身份证号"),
        UserMobile("user_mobile", "手机号"),

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
        IndividualRecordType("default__c", "个人"),
        EnterpriseRecordType("enterprise_record_type__c", "企业"),
        ;

        public String apiName;
        public String label;

        RecordType(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }
    }
}

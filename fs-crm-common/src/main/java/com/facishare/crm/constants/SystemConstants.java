package com.facishare.crm.constants;

public interface SystemConstants {
    String AccountApiName = "AccountObj";
    String PaymentApiName = "PaymentObj";
    String RefundApiName = "RefundObj";
    String ProductApiName = "ProductObj";
    String SalesOrderApiName = "SalesOrderObj";
    String SalesOrderProductApiName = "SalesOrderProductObj";

    String ObjectDescribeApiName = "object_describe_api_name";
    String ObjectDescribeId = "object_describe_id";

    enum RelevantTeam {
        TeamMemberRole("teamMemberRole", "成员角色"),

        TeamMemberPermissionType("teamMemberPermissionType", "成员权限类型"),

        TeamMemberEmployee("teamMemberEmployee", "成员员工"),

        ;

        public String apiName;
        public String label;

        RelevantTeam(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }
    }

    enum TeamMemberRole {
        Owner("1", "负责人"),

        Normal("2", "普通成员"),;

        public String value;
        public String label;

        TeamMemberRole(String value, String label) {
            this.value = value;
            this.value = label;
        }
    }

    enum Field {
        Id("_id", "主键id"), //查询的时候主键用这个<br>

        LifeStatusBeforeInvalid("life_status_before_invalid", "作废前生命状态"),

        LifeStatus("life_status", "生命状态"),

        LockStatus("lock_status", "锁定状态"),

        LockRule("lock_rule", "锁定规则"),

        LockUser("lock_user", "加锁人"),

        RelevantTeam("relevant_team", "相关团队"),

        Owner("owner", "负责人"),

        OwnerDepartment("owner_department", "负责人所在部门"),

        RecordType("record_type", "业务类型"),

        TennantID("tenant_id", "企业"),

        ExtendObjDataId("extend_obj_data_id", "extend_obj_data_id"),

        CreateBy("created_by", "创建人"),

        LastModifiedBy("last_modified_by", "最后修改人"),

        CreateTime("create_time", "创建时间"),

        LastModifiedTime("last_modified_time", "最后修改时间"),

        HelpText("help_text", "帮助信息")

        ;

        public String apiName;
        public String label;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }
    }

    enum LayoutType {
        Detail("detail"),

        List("list"),

        Add("add"),

        Edit("edit");

        public String layoutType;

        LayoutType(String layoutType) {
            this.layoutType = layoutType;
        }
    }

    enum RenderType {
        Text("text"),

        TrueOrFalse("true_or_false"),

        UseScope("use_scope"),

        AutoNumber("auto_number"),

        MasterDetail("master_detail"),

        Currency("currency"),

        SelectOne("select_one"),

        SelectMany("select_many"),

        Number("number"),

        Employee("employee"),

        RecordType("record_type"),

        ObjectReference("object_reference"),

        DateTime("date_time"),

        Date("date"),

        Quote("quote"),

        Image("image"),

        Percentile("percentile"),

        Country("country"),

        City("city"),

        Province("province"),

        District("district"),

        LongText("long_text"),

        Url("url"),

        Location("location");

        public String renderType;

        RenderType(String renderType) {
            this.renderType = renderType;
        }

    }

    enum ActionCode {
        Abolish("Abolish"),

        Edit("Edit"),

        ChangeOwner("ChangeOwner", "java_spring", "ChangeOwnerCustomAction", "更换负责人"),

        AddTeamMember("AddTeamMember", "java_spring", "AddTeamMemberCustomAction", "添加团队成员"),

        EditTeamMember("EditTeamMember", "java_spring", "EditTeamMemberCustomAction", "编辑团队成员"),

        DeleteTeamMember("DeleteTeamMember", "java_spring", "DeleteTeamMemberCustomAction", "删除团队成员"),

        Lock("Lock", "java_spring", "LockCustomAction", "锁定"),

        Unlock("Unlock", "java_spring", "UnLockCustomAction", "解锁"),

        ;

        String actionCode;
        String sourceType;
        String actionClass;
        String label;

        ActionCode(String actionCode) {
            this.actionCode = actionCode;
        }

        ActionCode(String actionCode, String sourceType, String actionClass, String label) {
            this.actionCode = actionCode;
            this.sourceType = sourceType;
            this.actionClass = actionClass;
            this.label = label;
        }
    }

    enum LifeStatus {
        Ineffective("ineffective", "未生效"),

        UnderReview("under_review", "审核中"),

        Normal("normal", "正常"),

        InChange("in_change", "变更中"),

        Invalid("invalid", "作废");

        public String value;
        public String label;

        LifeStatus(String value, String label) {
            this.value = value;
            this.label = label;
        }

    }

    enum LockStatus {
        Locked("1", "锁定"),

        UnLock("0", "未锁定"),

        ;

        public String value;
        public String label;

        LockStatus(String value, String label) {
            this.label = label;
            this.value = value;
        }
    }
}

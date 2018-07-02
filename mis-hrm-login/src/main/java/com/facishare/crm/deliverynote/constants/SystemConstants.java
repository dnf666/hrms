package com.facishare.crm.deliverynote.constants;

import lombok.Getter;

public interface SystemConstants {

    @Getter
    enum ActionCode {
        Abolish("Abolish"),
        Add("Add"),
        Edit("Edit"),

        ChangeOwner("ChangeOwner", "java_spring", "ChangeOwnerCustomAction", "更换负责人"),

        AddTeamMember("AddTeamMember", "java_spring", "AddTeamMemberCustomAction", "添加团队成员"),

        EditTeamMember("EditTeamMember", "java_spring", "EditTeamMemberCustomAction", "编辑团队成员"),

        DeleteTeamMember("DeleteTeamMember", "java_spring", "DeleteTeamMemberCustomAction", "删除团队成员"),

        Lock("Lock", "java_spring", "LockCustomAction", "锁定"),

        Unlock("Unlock", "java_spring", "UnLockCustomAction", "解锁"),

        ;

        private String actionCode;
        private String sourceType;
        private String actionClass;
        private String label;

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

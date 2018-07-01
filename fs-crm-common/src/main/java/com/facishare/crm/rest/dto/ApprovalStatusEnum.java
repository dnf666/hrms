package com.facishare.crm.rest.dto;

public enum ApprovalStatusEnum {
    IN_PROGRESS("in_progress"),

    PASS("pass"),

    ERROR("error"),

    CANCEL("cancel"),

    REJECT("reject");

    private String value;

    ApprovalStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

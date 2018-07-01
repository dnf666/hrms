package com.facishare.crm.customeraccount.enums;

/**
 * Created by xujf on 2017/12/15.
 */
public enum RebateActionEnum {

    Effective("生效", "Effective"),

    Disable("失效", "Disable"),

    Recover("恢复", "Recover"),

    Edit("编辑", "Edit");

    private String label;
    private String value;

    RebateActionEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}

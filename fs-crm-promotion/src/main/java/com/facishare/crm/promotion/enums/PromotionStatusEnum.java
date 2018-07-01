package com.facishare.crm.promotion.enums;

public enum PromotionStatusEnum {
    ENABLE("1", "启用"),

    DISABLE("2", "禁用");

    public String value;
    public String label;

    PromotionStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

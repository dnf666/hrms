package com.facishare.crm.promotion.enums;

public enum GiftTypeEnum {
    NormalProduct("1", "普通商品"),

    Self("2", "本品");

    public String value;
    public String label;

    GiftTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

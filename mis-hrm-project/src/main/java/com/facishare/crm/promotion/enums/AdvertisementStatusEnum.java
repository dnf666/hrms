package com.facishare.crm.promotion.enums;

public enum AdvertisementStatusEnum {
    OnLine("1", "已上线"),

    Offline("2", "已下线");

    public String value;
    public String label;

    AdvertisementStatusEnum(String value, String label) {
        this.label = label;
        this.value = value;
    }
}

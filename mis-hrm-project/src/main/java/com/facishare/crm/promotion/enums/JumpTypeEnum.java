package com.facishare.crm.promotion.enums;

public enum JumpTypeEnum {
    PromotionDetail("1", "促销详情"),

    ProductDetail("2", "商品详情"),

    ExternalLink("3", "外部链接"),

    ;

    public String value;
    public String label;

    JumpTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

package com.facishare.crm.promotion.enums;

public enum PromotionRuleRecordTypeEnum {
    ProductPromotion("product_promotion__c", "商品促销"),

    OrderPromotion("default__c", "订单促销");

    public String apiName;
    public String label;

    PromotionRuleRecordTypeEnum(String apiName, String label) {
        this.apiName = apiName;
        this.label = label;
    }
}

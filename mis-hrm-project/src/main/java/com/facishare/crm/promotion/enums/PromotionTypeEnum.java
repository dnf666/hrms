package com.facishare.crm.promotion.enums;

import java.util.Set;

import com.google.common.collect.Sets;

public enum PromotionTypeEnum {
    //1-4 商品促销
    PriceDiscount("1", "打折"),

    DerateMoney("2", "减免"),

    FixedPrice("3", "一口价"),

    NumberReachedGift("4", "买赠"),

    //11-13订单促销
    OrderDiscount("11", "满折"),

    OrderDerateMoney("12", "满减"),

    OrderMoneyReachedGift("13", "满赠"),

    ;

    public String value;
    public String label;

    PromotionTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static PromotionTypeEnum get(String promotionType) {
        for (PromotionTypeEnum typeEnum : values()) {
            if (typeEnum.value.equals(promotionType)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static boolean isProductPromotion(String value) {
        Set<String> productPromotionValues = Sets.newHashSet(PriceDiscount.value, DerateMoney.value, FixedPrice.value, NumberReachedGift.value);
        if (productPromotionValues.contains(value)) {
            return true;
        } else {
            return false;
        }

    }
}

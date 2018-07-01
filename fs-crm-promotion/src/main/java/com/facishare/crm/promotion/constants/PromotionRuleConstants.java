package com.facishare.crm.promotion.constants;

public interface PromotionRuleConstants {
    String API_NAME = "PromotionRuleObj";
    String DISPLAY_NAME = "促销规则";
    String DEFAULT_LAYOUT_API_NAME = "PromotionRule_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "PromotionRule_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "promotion_rule";
    Integer ICON_INDEX = null;

    enum Field {
        Name("name", "规则编号"),

        Promotion("promotion_id", "促销活动", "target_related_list_promotion_rule_promotion", "促销规则"),

        PurchaseNum("purchase_num", "购买数量满"),

        FixedPrice("fixed_price", "一口价价格(元)"),

        DerateMoney("derate_money", "减免金额(元)"),

        PriceDiscount("price_discount", "价格折扣"),

        OrderMoney("order_money", "订单金额满(元)"),

        OrderDiscount("order_discount", "订单折扣"),

        OrderDerateMoney("order_derate_money", "订单减免金额(元)"),

        GiftProduct("gift_product_id", "赠品", "target_related_list_promotion_rule_gift_product", "促销规则"),

        GiftProductNum("gift_product_num", "赠品数量"),

        //6.3
        GiftType("gift_type", "赠品类型"),

        ;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        Field(String apiName, String label, String targetRelatedListName, String targetRelatedListLabel) {
            this.apiName = apiName;
            this.label = label;
            this.targetRelatedListName = targetRelatedListName;
            this.targetRelatedListLabel = targetRelatedListLabel;
        }

        public String apiName;
        public String label;
        public String targetRelatedListName;
        public String targetRelatedListLabel;
    }
}

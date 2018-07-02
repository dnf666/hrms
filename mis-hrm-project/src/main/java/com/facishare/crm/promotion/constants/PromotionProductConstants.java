package com.facishare.crm.promotion.constants;

public interface PromotionProductConstants {
    String API_NAME = "PromotionProductObj";
    String DISPLAY_NAME = "促销产品";
    String DEFAULT_LAYOUT_API_NAME = "PromotionProduct_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "PromotionProduct_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "promotion_product";
    Integer ICON_INDEX = null;

    enum Field {
        Name("name", "自增编号"),

        Promotion("promotion_id", "促销活动", "target_related_list_promotion_product_promotion", "促销产品"),

        Product("product_id", "促销商品", "target_related_list_promotion_product_product", "促销产品"),

        Price("price", "产品价格(元)"),

        Specification("specification", "规格"),

        Unit("unit", "单位"),

        ProductStatus("product_status", "状态"),

        //6.3
        Quota("quota", "促销总量限额");

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

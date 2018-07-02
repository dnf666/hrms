package com.facishare.crm.promotion.constants;

public interface AdvertisementConstants {
    String API_NAME = "AdvertisementObj";
    String DISPLAY_NAME = "广告";
    String DEFAULT_LAYOUT_API_NAME = "Advertisement_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";

    String LIST_LAYOUT_API_NAME = "Advertisement_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "advertisement";
    int ICON_INDEX = 22;

    enum Field {
        Name("name", "编号"),

        AdPictures("ad_pictures", "广告图片"),

        JumpType("jump_type", "跳转类型"),

        JumpAddress("jump_address", "跳转地址"),

        Promotion("promotion_id", "跳转类型(促销)", "target_related_list_advertisement_promotion", "广告"),

        Product("product_id", "跳转类型(商品)", "target_related_list_advertisement_product", "广告"),

        Status("status", "上线状态"),

        Sort("sort", "排序"),

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

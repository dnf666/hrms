package com.facishare.crm.stock.constants;

/**
 * Created by linchf on 2018/1/9.
 */
public interface StockConstants {
    String API_NAME = "StockObj";
    String DISPLAY_NAME = "库存";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "stock";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "库存ID"),

        Product("product_id", "产品名称", "target_related_list_stock_product", "库存"),

        Product_Status("product_status", "产品状态"),

        Is_Give_Away("is_give_away", "是否赠品"),

        Specs("specs", "规格"),

        Unit("unit", "单位"),

        Category("category", "分类"),

        RealStock("real_stock", "实际库存"),

        AvailableStock("available_stock", "可用库存"),

        BlockedStock("blocked_stock", "冻结库存"),

        SafetyStock("safety_stock", "安全库存"),

        Warehouse("warehouse_id", "所属仓库", "target_related_list_stock_warehouse", "库存");

        public String apiName;
        public String label;
        public String targetRelatedListName;
        public String targetRelatedListLabel;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        Field(String apiName, String label, String targetRelatedListName, String targetRelatedListLabel) {
            this.apiName = apiName;
            this.label = label;
            this.targetRelatedListName = targetRelatedListName;
            this.targetRelatedListLabel = targetRelatedListLabel;
        }
    }
}

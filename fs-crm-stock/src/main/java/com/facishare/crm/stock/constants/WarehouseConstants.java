package com.facishare.crm.stock.constants;

/**
 * Created by linchf on 2018/1/9.
 */
public interface WarehouseConstants {
    String API_NAME = "WarehouseObj";
    String DISPLAY_NAME = "仓库";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";
    String ACCOUNT_RANGE_EXPRESSION_TYPE = "long_text";
    String ACCOUNT_RANGE_DEFAULT_VALUE = "{\"type\":\"noCondition\",\"value\":\"ALL\"}";

    String CUSTOMER_RANGE_SECTION_API_NAME = "customer_range_section__c";
    String STORE_TABLE_NAME = "warehouse";

    String ADDRESS_FIELD_SECTION_DISPLAY_NAME = "仓库地址";

    int ICON_INDEX = 15;

    enum Field {

        Name("name", "仓库名称"),

        Number("number", "仓库编号"),

        Area("area", "仓库地址"),

        Country("country", "国家"),

        Province("province", "省"),

        City("city", "市"),

        District("district", "区"),

        Address("address", "详细地址"),

        Is_Default("is_default", "是否默认仓"),

        Account_range("account_range", "适用客户"),

        Is_Enable("is_enable", "启用状态"),

        Remark("remark", "备注"),

        Location("location", "定位");

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

package com.facishare.crm.erpstock.constants;

/**
 * @author liangk
 * @date 08/05/2018
 */
public interface ErpWarehouseConstants {
    String API_NAME = "ErpWarehouseObj";
    String DISPLAY_NAME = "ERP仓库";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "erp_warehouse";

    String ADDRESS_FIELD_SECTION_DISPLAY_NAME = "仓库地址";

    int ICON_INDEX = 15;

    enum Field {

        Name("name", "仓库名称"),

        Area("area", "仓库地址"),

        Country("country", "国家"),

        Province("province", "省"),

        City("city", "市"),

        District("district", "区"),

        Address("address", "详细地址"),

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

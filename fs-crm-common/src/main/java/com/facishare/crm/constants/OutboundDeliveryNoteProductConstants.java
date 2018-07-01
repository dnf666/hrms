package com.facishare.crm.constants;

/**
 * @author linchf
 * @date 2018/3/14
 */
public interface OutboundDeliveryNoteProductConstants {
    String API_NAME = "OutboundDeliveryNoteProductObj";
    String DISPLAY_NAME = "出库单产品";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "outbound_delivery_note_product";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "出库单产品ID"),

        Outbound_Delivery_Note("outbound_delivery_note_id", "出库单编号", "target_related_list_odn_product_odn", "出库单产品"),

        Product("product_id", "产品名称", "target_related_list_odnp_p", "出库单产品"),

        Specs("specs", "规格"),

        Unit("unit", "单位"),

        Outbound_Amount("outbound_amount", "本次出库数量"),

        Stock("stock_id", "库存", "target_related_list_odn_product_stock", "出库单产品"),

        Available_stock("available_stock", "可用库存"),

        Remark("remark", "备注");

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

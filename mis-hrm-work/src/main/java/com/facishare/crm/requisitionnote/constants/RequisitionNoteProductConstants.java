package com.facishare.crm.requisitionnote.constants;

/**
 * @author liangk
 * @date 12/03/2018
 */
public interface RequisitionNoteProductConstants {
    String API_NAME = "RequisitionNoteProductObj";
    String DISPLAY_NAME = "调拨单产品";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "requisition_note_product";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "调拨单产品ID"),

        Requisition("requisition_note_id", "调拨单编号", "target_related_list_requisition", "调拨单产品"),

        Product("product_id", "产品名称", "target_related_list_rp", "调拨单产品"),

        Stock("stock_id", "库存", "target_related_list_rps", "调拨单产品"),

        RequisitionProductAmount("requisition_product_amount", "调拨产品数量"),

        AvailableStock("available_stock", "可用库存"),

        Specs("specs", "规格"),

        Unit("unit", "单位"),

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

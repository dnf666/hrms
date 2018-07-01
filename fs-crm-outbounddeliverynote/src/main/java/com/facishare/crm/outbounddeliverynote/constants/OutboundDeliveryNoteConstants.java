package com.facishare.crm.outbounddeliverynote.constants;

/**
 * @author linchf
 * @date 2018/3/14
 */
public interface OutboundDeliveryNoteConstants {
    String API_NAME = "OutboundDeliveryNoteObj";
    String DISPLAY_NAME = "出库单";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "outbound_delivery_note";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "出库单编号"),

        Outbound_Date("outbound_date", "出库日期"),

        Warehouse("warehouse_id", "所属仓库", "target_related_list_outbound_wh_wh", "出库单"),

        Outbound_Type("outbound_type", "出库类型"),

        Delivery_Note("delivery_note_id", "发货单编号", "target_related_list_outbound_dn_dn", "出库单"),

        Requisition_Note("requisition_note_id", "调拨单编号", "target_related_list_outbound_rn_rn", "出库单"),

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

package com.facishare.crm.stock.constants;

/**
 * Created by linchf on 2018/1/9.
 */
public interface GoodsReceivedNoteConstants {
    String API_NAME = "GoodsReceivedNoteObj";
    String DISPLAY_NAME = "入库单";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "goods_received_note";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "入库单编号"),

        GoodsReceivedDate("goods_received_date", "入库日期"),

        Warehouse("warehouse_id", "所属仓库", "target_related_list_note_wh_wh", "入库单"),

        RequisitionNote("requisition_note_id", "调拨单编号", "target_related_list_rn_grn", "入库单"),

        GoodsReceivedType("goods_received_type", "入库类型"),

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

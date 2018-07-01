package com.facishare.crm.requisitionnote.constants;

/**
 * @author liangk
 * @date 12/03/2018
 */
public interface RequisitionNoteConstants {
    String API_NAME = "RequisitionNoteObj";
    String DISPLAY_NAME = "调拨单";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "requisition_note";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "调拨单编号"),

        RequisitionDate("requisition_date", "调拨日期"),

        TransferOutWarehouse("transfer_out_warehouse_id", "调出仓库", "target_related_list_tow", "调拨单(调出)"),

        TransferInWarehouse("transfer_in_warehouse_id", "调入仓库", "target_related_list_tiw", "调拨单(调入)"),

        InboundConfirmed("inbound_confirmed", "是否已确认入库"),

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

    enum Button {
        InboundConfirmed("InboundConfirmed", "确认入库");

        public String apiName;
        public String label;

        Button(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }
    }
}

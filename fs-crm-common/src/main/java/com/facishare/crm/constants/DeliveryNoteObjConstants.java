package com.facishare.crm.constants;

/**
 * 发货单对象
 * Created by chenzs on 2018/1/8.
 */
public interface DeliveryNoteObjConstants {
    String API_NAME = "DeliveryNoteObj";
    String DISPLAY_NAME = "发货单";
    String DEFAULT_LAYOUT_API_NAME = "DeliveryNoteObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "DeliveryNoteObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "delivery_note";
    int ICON_INDEX = 19;

    enum Field {
        Name("name", "发货单编号"),
        SalesOrderId("sales_order_id", "销售订单编号", "target_related_list_dn_sales_order_id", "发货单"),
        DeliveryDate("delivery_date", "发货日期"),
        ExpressOrg("express_org", "物流公司"),
        ExpressOrderId("express_order_id", "物流单号"),

        TotalDeliveryMoney("total_delivery_money", "发货总金额"),
        DeliveryWarehouseId("delivery_warehouse_id", "发货仓库", "target_related_list_dn_delivery_warehouse_id", "发货单"),
        Remark("remark", "备注"),
        ReceiveDate("receive_date", "收货日期"),
        ReceiveRemark("receive_remark", "收货备注"),

        Status("status", "状态");

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
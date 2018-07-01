package com.facishare.crm.constants;

/**
 * 发货单产品对象
 * Created by chenzs on 2018/1/9.
 */
public interface DeliveryNoteProductObjConstants {
    String API_NAME = "DeliveryNoteProductObj";
    String DISPLAY_NAME = "发货单产品";
    String DEFAULT_LAYOUT_API_NAME = "DeliveryNoteProductObj_default_layout__c";
    String DEFAULT_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = "DeliveryNoteProductObj_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "delivery_note_product";
    int ICON_INDEX = 19;

    enum Field {
        Name("name", "发货单产品id"),
        DeliveryNoteId("delivery_note_id", "发货单编号", "target_related_list_dnp_delivery_note_id", "发货单产品"), //dnp：delivery_note_product的缩写，不然太长了，会报错
        SalesOrderId("sales_order_id", "销售订单编号", "target_related_list_dnp_sales_order_id", "发货单产品"),
        ProductId("product_id", "产品名称", "target_related_list_dnp_product_id", "发货单产品"),

        Specs("specs", "产品规格"),
        Unit("unit", "单位"),

        OrderProductAmount("order_product_amount", "订单产品数量"),
        HasDeliveredNum("has_delivered_num", "已发货数"),
        DeliveryNum("delivery_num", "本次发货数"),
        AvgPrice("avg_price", "平均单价"),
        DeliveryMoney("delivery_money", "本次发货金额"),

        StockId("stock_id", "库存", "target_related_list_dnp_stock_id", "发货单产品"),
        RealStock("real_stock", "实际库存"),
        RealReceiveNum("real_receive_num", "本次收货数"),

        Remark("remark", "备注"),
        ReceiveRemark("receive_remark", "收货备注");

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

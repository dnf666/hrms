package com.facishare.crm.stock.constants;

/**
 * 入库单产品
 * Created by linchf on 2018/1/9.
 */
public interface GoodsReceivedNoteProductConstants {
    String API_NAME = "GoodsReceivedNoteProductObj";
    String DISPLAY_NAME = "入库单产品";
    String DETAIL_LAYOUT_API_NAME = API_NAME + "_default_layout__c";
    String DETAIL_LAYOUT_DISPLAY_NAME = "默认布局";
    String LIST_LAYOUT_API_NAME = API_NAME + "_list_layout__c";
    String LIST_LAYOUT_DISPLAY_NAME = "移动端默认列表页";

    String STORE_TABLE_NAME = "goods_received_note_product";
    int ICON_INDEX = 15;

    enum Field {

        Name("name", "入库产品ID"),

        GoodsReceivedNote("goods_received_note_id", "入库单编号", "target_related_list_product_note", "入库单产品"), //target_related_list_customer__c

        Product("product_id", "产品名称", "target_related_list_product_product", "入库单产品"),

        IsGiveAway("is_give_away", "是否赠品"),

        Specs("specs", "规格"),

        Unit("unit", "单位"),

        GoodsReceivedAmount("goods_received_amount", "入库数量"),

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

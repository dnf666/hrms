package com.facishare.crm.deliverynote.predefine.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DeliveryNoteProductVO {
    @SerializedName("_id")
    @JsonProperty("_id")
    @org.codehaus.jackson.annotate.JsonProperty("_id")
    @JSONField(name="_id")
    private String id;

    private String name;

    @SerializedName("delivery_note_id")
    @JsonProperty("delivery_note_id")
    @org.codehaus.jackson.annotate.JsonProperty("delivery_note_id")
    @JSONField(name="delivery_note_id")
    private String deliveryNoteId;

    @SerializedName("sales_order_id")
    @JsonProperty("sales_order_id")
    @org.codehaus.jackson.annotate.JsonProperty("sales_order_id")
    @JSONField(name="sales_order_id")
    private String salesOrderId;

    @SerializedName("sales_order_id__r")
    @JsonProperty("sales_order_id__r")
    @org.codehaus.jackson.annotate.JsonProperty("sales_order_id__r")
    @JSONField(name="sales_order_id__r")
    private String salesOrderName;

    @SerializedName("product_id")
    @JsonProperty("product_id")
    @org.codehaus.jackson.annotate.JsonProperty("product_id")
    @JSONField(name="product_id")
    private String productId;

    @SerializedName("product_id__r")
    @JsonProperty("product_id__r")
    @org.codehaus.jackson.annotate.JsonProperty("product_id__r")
    @JSONField(name="product_id__r")
    private String productName;

    private String specs;

    private String unit;
    @SerializedName("avg_price")
    @JsonProperty("avg_price")
    @org.codehaus.jackson.annotate.JsonProperty("avg_price")
    @JSONField(name="avg_price")
    private BigDecimal avgPrice;

    @SerializedName("order_product_amount")
    @JsonProperty("order_product_amount")
    @org.codehaus.jackson.annotate.JsonProperty("order_product_amount")
    @JSONField(name="order_product_amount")
    private BigDecimal orderProductAmount;

    @SerializedName("has_delivered_num")
    @JsonProperty("has_delivered_num")
    @org.codehaus.jackson.annotate.JsonProperty("has_delivered_num")
    @JSONField(name="has_delivered_num")
    private BigDecimal hasDeliveredNum;

    @SerializedName("delivery_num")
    @JsonProperty("delivery_num")
    @org.codehaus.jackson.annotate.JsonProperty("delivery_num")
    @JSONField(name="delivery_num")
    private BigDecimal deliveryNum;

    @SerializedName("delivery_money")
    @org.codehaus.jackson.annotate.JsonProperty("delivery_money")
    @JSONField(name="delivery_money")
    private BigDecimal deliveryMoney;
    // 库存ID
    @SerializedName("stock_id")
    @JsonProperty("stock_id")
    @org.codehaus.jackson.annotate.JsonProperty("stock_id")
    @JSONField(name="stock_id")
    private String stockId;

    // 库存主属性
    @SerializedName("stock_id__r")
    @JsonProperty("stock_id__r")
    @org.codehaus.jackson.annotate.JsonProperty("stock_id__r")
    @JSONField(name="stock_id__r")
    private String stockName;

    @SerializedName("real_stock")
    @JsonProperty("real_stock")
    @org.codehaus.jackson.annotate.JsonProperty("real_stock")
    @JSONField(name="real_stock")
    private BigDecimal realStock;

    @SerializedName("real_receive_num")
    @JsonProperty("real_receive_num")
    @org.codehaus.jackson.annotate.JsonProperty("real_receive_num")
    @JSONField(name="real_receive_num")
    private BigDecimal realReceiveNum;

    @SerializedName("receive_remark")
    @JsonProperty("receive_remark")
    @org.codehaus.jackson.annotate.JsonProperty("receive_remark")
    @JSONField(name="receive_remark")
    private BigDecimal receiveRemark;

    private String remark;
}

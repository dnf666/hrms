package com.facishare.crm.deliverynote.predefine.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryNoteVO {
    @SerializedName("_id")
    @JsonProperty("_id")
    private String id;
    private String name;

    @SerializedName("sales_order_id")
    @JsonProperty("sales_order_id")
    private String salesOrderId;

    @SerializedName("delivery_date")
    @JsonProperty("delivery_date")
    private Long deliveryDate;

    @SerializedName("express_org")
    @JsonProperty("express_org")
    private String expressOrg;

    @SerializedName("express_order_id")
    @JsonProperty("express_order_id")
    private String expressOrderId;

    private String remark;
    /**
     * @see DeliveryNoteObjStatusEnum
     */
    private String status;

    @SerializedName("delivery_warehouse_id")
    @JsonProperty("delivery_warehouse_id")
    private String deliveryWarehouseId;

    @SerializedName("total_delivery_money")
    @JsonProperty("total_delivery_money")
    @JSONField(name="total_delivery_money")
    private BigDecimal totalDeliveryMoney;

    @SerializedName("receive_date")
    @JsonProperty("receive_date")
    @JSONField(name="receive_date")
    private Long receiveDate;

    @SerializedName("receive_remark")
    @JsonProperty("receive_remark")
    @JSONField(name="receive_remark")
    private BigDecimal receiveRemark;
}

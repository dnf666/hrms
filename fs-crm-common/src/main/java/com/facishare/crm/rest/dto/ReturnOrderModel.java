package com.facishare.crm.rest.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by linchf on 2018/1/16.
 */
public class ReturnOrderModel {

    @Data
    public static class GetByIdResult {
        private ReturnOrderVo value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class GetByIdsResult {
        private List<ReturnOrderVo> value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class Result {
        private List<ReturnOrderVo> value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class ReturnOrderVo {
        @SerializedName("CustomerTradeID")
        private String customerTradeId;
        @SerializedName("CustomerID")
        private String customerId;
        @SerializedName("ReturnOrderID")
        private String returnOrderId;
        @SerializedName("ReturnOrderCode")
        private String returnOrderCode;

        @SerializedName("TradeCode")
        private String tradeCode;
        @SerializedName("ReturnWarehouseID")
        private String warehouseId;
        @SerializedName("IsDeleted")
        private Boolean isDeleted;
        @SerializedName("Status")
        private Integer status;
    }


    @Data
    public static class QueryReturnOrderProductArg {
        @SerializedName("Offset")
        private Integer offset;
        @SerializedName("Limit")
        private Integer limit;
        @SerializedName("Conditions")
        private List<Condition> conditions;

        @Data
        public static class Condition {
            @SerializedName("ConditionType")
            private String conditionType;
            @SerializedName("Conditions")
            ReturnOrderVo conditions;
        }
    }

    @Data
    public static class QueryReturnOrderArg {
        @SerializedName("Offset")
        private Integer offset;
        @SerializedName("Limit")
        private Integer limit;
        @SerializedName("Conditions")
        private List<Condition> conditions;

        @Data
        public static class Condition {
            @SerializedName("ConditionType")
            private String conditionType;
            @SerializedName("Conditions")
            ReturnOrderVo conditions;
        }
    }

    @Data
    public static class QueryReturnOrderProductResult {
        private boolean success;
        private String message;
        private int errorCode;
        List<ReturnOrderProductVO> value = Lists.newArrayList();
    }

    @Data
    public static class QueryReturnOrderByConditionResult {
        private boolean success;
        private String message;
        private int errorCode;
        private ReturnOrderPageData value;
    }

    @Data
    public static class ReturnOrderPageData {
        @SerializedName("Items")
        List<ReturnOrderVo> items;
    }

    @Data
    public static class ReturnOrderProductVO {
        @SerializedName("ReturnOrderID")
        private String returnOrderID;
        @SerializedName("ProductID")
        private String productId;
        @SerializedName("Product")
        private ReturnOrderProductDetailVO productDetail;
        @SerializedName("Amount")
        private BigDecimal amount;
    }

    @Data
    public static class ReturnOrderProductDetailVO {
        @SerializedName("ProductID")
        private String productId;
        @SerializedName("Name")
        private String name;
    }

}

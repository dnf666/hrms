package com.facishare.crm.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;

public class SalesOrderModel {
    public enum SalesOrderStatusEnum {
        HAS_CONFIRMED(7, "已确认"), INVALID(99, "已作废"),;

        @Getter
        private int code;
        @Getter
        private String desc;

        SalesOrderStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Data
    public static class Arg {
        @SerializedName("ObjectType")
        private int objectType;
        @SerializedName("IDs")
        private List<String> ids;
        @SerializedName("IncludeUserDefinedFields")
        private boolean includeUserDefinedFields;
        @SerializedName("IncludeCalculationFields")
        private boolean includeCalculationFields;
    }

    @Data
    public static class Result {
        private List<SalesOrderVo> value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class SaveCustomerOrderResult {
        private boolean success;
        private String message;
        private int errorCode;
    }



    @Data
    public static class GetByIdResult {
        private SalesOrderVo value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class GetByIdsResult {
        private List<SalesOrderVo> value = Lists.newArrayList();
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class QueryOrderProductArg {
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
            SalesOrderVo conditions;
        }
    }

    @Data
    public static class QueryOrderProductResult {
        private boolean success;
        private String message;
        private int errorCode;
        List<SalesOrderProductVO> value = Lists.newArrayList();
    }

    @Data
    public static class SetLogisticsStatusArg {
        @SerializedName("ObjectId")
        private String objectId;
        @SerializedName("LogisticsStatus")
        private Integer logisticsStatus;
    }

    @Data
    public static class SetLogisticsStatusResult {
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class ExistsDeliveredOrders {
        private boolean value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class UpdateCustomerOrderDeliveryToReceivedTask {
        private boolean value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class SalesOrderVo {
        @SerializedName("_id")
        private String id;
        @SerializedName("CustomerTradeID")
        private String customerTradeId;
        @SerializedName("CustomerID")
        private String customerId;
        @SerializedName("TradeCode")
        private String tradeCode;
        @SerializedName("ShippingWarehouseID")
        private String warehouseId;
        @SerializedName("IsDeleted")
        private Boolean isDeleted;
        @SerializedName("Status")
        private Integer status;
        private String promotionId;
        @SerializedName("TradeMoney")
        private BigDecimal tradeMoney;
        @SerializedName("LogisticsStatus")
        private Integer logisticsStatus;
        @SerializedName("DeliveredAmountSum")
        private BigDecimal deliveredAmountSum;
        @SerializedName("ConfirmReceiveTime")
        private Long confirmReceiveTime;
        @SerializedName("ConfirmDeliveryTime")
        private Long confirmDeliveryTime;
        @SerializedName("CreateTime")
        private Long createTime;
    }

    @Data
    public static class SalesOrderProductVO {
        @SerializedName("TradeProductID")
        private String tradeProductId;
        @SerializedName("CustomerTradeID")
        private String customerTradeId;
        @SerializedName("ProductID")
        private String productId;
        @SerializedName("ProductName")
        private String productName;
        @SerializedName("Amount")
        private BigDecimal amount;
        @SerializedName("RecordType")
        private String recordType;
        @SerializedName("Price")
        private BigDecimal price;
        @SerializedName("SubTotal")
        private BigDecimal subTotal;
    }

    @Data
    public static class CheckStockEnableResult {
        @SerializedName("value")
        private boolean value;
        @SerializedName("success")
        private boolean success;
        @SerializedName("message")
        private String message;
        @SerializedName("errorCode")
        private int errorCode;
    }
}

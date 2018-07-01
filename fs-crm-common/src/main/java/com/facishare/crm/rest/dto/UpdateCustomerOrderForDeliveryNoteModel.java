package com.facishare.crm.rest.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class UpdateCustomerOrderForDeliveryNoteModel {
    @Data
    public static class Arg {
        @SerializedName("CustomerTradeID")
        private String customerTradeId;
        /**
         * 发货状态
         * @See com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum
         */
        @SerializedName("LogisticsStatus")
        private Integer logisticsStatus;
        /**
         * 已发货金额
         */
        @SerializedName("DeliveredAmountSum")
        private BigDecimal deliveredAmountSum;
        @SerializedName("ConfirmReceiveTime")
        private Long confirmReceiveTime;
        @SerializedName("ConfirmDeliveryTime")
        private Long confirmDeliveryTime;
        @SerializedName("UpdateDetailList")
        private List<Product> updateDetailList;

        @Data
        public static class Product {
            @SerializedName("TradeProductID")
            private String tradeProductID;
            /**
             * 已发货数
             */
            @SerializedName("DeliveredCount")
            private BigDecimal deliveredCount;
            /**
             * 已发货金额小计
             */
            @SerializedName("DeliveryAmount")
            private BigDecimal deliveryAmount;
        }
    }

    @Data
    public static class Result {
        private boolean success;
        private String message;
        private int errorCode;
        private boolean value;
    }
}

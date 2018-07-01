package com.facishare.crm.rest.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

public class BatchGetPromotionProductQuantity {
    @Data
    public static class Arg {
        @SerializedName("PromotionID")
        private String promotionId;
        @SerializedName("ProductID")
        private String productId;
    }

    @Data
    public static class PromotionProductArg {
        @SerializedName("PromotionID")
        private String promotionId;
        @SerializedName("ProductID")
        private String productId;
    }

    @Data
    public static class Result {
        private boolean success;
        private String message;
        private int errorCode;
        private List<PromotionProductQuantity> value;
    }

    @Data
    public static class PromotionProductQuantity {
        @SerializedName("PromotionID")
        private String promotionId;
        @SerializedName("ProductID")
        private String productId;
        @SerializedName("Quantity")
        private Integer quantity;
    }
}

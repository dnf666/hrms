package com.facishare.crm.rest.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

public class ObjectRelatedPromotionModel {
    @Data
    public static class Arg {
        List<String> promotionIds;
    }

    @Data
    public static class Result {
        private List<RelatedPromotionVo> value;
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class RelatedPromotionVo {
        @SerializedName("_id")
        private String id;
        @SerializedName("ProductName")
        private String productName;
        @SerializedName("TradeCode")
        private String tradeCode;
        @SerializedName("ObjectType")
        private int objectType;
        @SerializedName("PromotionID")
        private String promotionId;
    }
}

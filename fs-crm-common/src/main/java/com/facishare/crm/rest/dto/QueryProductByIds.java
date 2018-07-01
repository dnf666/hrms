package com.facishare.crm.rest.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liangk
 */
public class QueryProductByIds {
    @Data
    public static class Arg {
        List<String> productIds;
    }

    @Data
    public static class Result {
        private List<ProductVO> value = Lists.newArrayList();
        private boolean success;
        private String message;
        private int errorCode;
    }

    @Data
    public static class ProductVO {
        @SerializedName("ProductID")
        private String id;
        @SerializedName("Name")
        private String productName;
        @SerializedName("IsDeleted")
        private Boolean isDeleted;
        @SerializedName("SafetyStock")
        private BigDecimal safetyStock;
        @SerializedName("Unit")
        private String unit;
        @SerializedName("Specs")
        private String specs;
        @SerializedName("Price")
        private BigDecimal price;
    }
}


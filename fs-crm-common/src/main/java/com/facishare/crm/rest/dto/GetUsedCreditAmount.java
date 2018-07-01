package com.facishare.crm.rest.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

public class GetUsedCreditAmount {

    @Data
    public static class Arg {
        @SerializedName("CustomerID")
        String customerID;
    }

    @Data
    public static class Result {
        private boolean success;
        private String message;
        private Integer errorCode;
        private Value value;

        @Data
        public static class Value {
            @SerializedName("UsedCreditAmount")
            private double usedCreditAmount;
        }

    }
}

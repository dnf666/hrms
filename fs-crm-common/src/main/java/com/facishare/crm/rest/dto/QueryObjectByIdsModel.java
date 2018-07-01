package com.facishare.crm.rest.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 23/03/2018
 */
public class QueryObjectByIdsModel {
    @Data
    public static class Arg {
        @SerializedName("IDs")
        @JsonProperty("IDs")
        @JSONField(name="IDs")
        private List<String> productIds;

        @SerializedName("ObjectType")
        @JsonProperty("ObjectType")
        @JSONField(name="ObjectType")
        private int objectType;

        @SerializedName("IncludeUserDefinedFields")
        @JsonProperty("IncludeUserDefinedFields")
        @JSONField(name="IncludeUserDefinedFields")
        private boolean includeUserDefinedFields;

        @SerializedName("IncludeCalculationFields")
        @JsonProperty("IncludeCalculationFields")
        @JSONField(name="IncludeCalculationFields")
        private boolean includeCalculationFields;

    }

    @Data
    public static class Result {
        private List<Map<String, Object>> value;
        private boolean success;
        private String message;
        private int errorCode;
    }
}
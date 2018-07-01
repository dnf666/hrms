package com.facishare.crm.sfa.utilities.proxy.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/2/24.
 */
public interface BatchAddSpecModel {
    @Data
    class Arg {
        @JsonProperty(value = "ProductID")
        @SerializedName(value = "ProductID")
        String productId;
        @JsonProperty(value = "SpecProductInfoList")
        @SerializedName(value = "SpecProductInfoList")
        List specProductInfoList;
    }

    @Data
    class Result {
        String message;
        Integer errorCode;
        Object value;
        Boolean success;
    }
}

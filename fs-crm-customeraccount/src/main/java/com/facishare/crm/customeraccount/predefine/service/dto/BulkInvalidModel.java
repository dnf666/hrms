package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

public class BulkInvalidModel {

    @Data
    public static class Arg {
        private List<InvalidArg> dataList;
    }

    @Data
    public static class Result {
        private List<ObjectDataDocument> objectDatas;
    }

    @Data
    public static class InvalidArg {
        @SerializedName("_id")
        private String id;
        @SerializedName("object_describe_api_name")
        private String objectDescribeApiName;
        @SerializedName("object_describe_id")
        private String objectDescribeId;
        private String lifeStatus;
    }
}

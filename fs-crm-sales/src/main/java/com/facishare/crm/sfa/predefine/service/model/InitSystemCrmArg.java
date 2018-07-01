package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

public interface InitSystemCrmArg {
    @Data
    class Arg {
        @JsonProperty("tenantIds")
        private String tenantIds;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private Map<String, ResultModel> result;
    }

    @Data
    class ResultModel {
        @JSONField(name = "M1")
        private Boolean flag;
        @JSONField(name = "M2")
        private String errorMsg;
    }
}

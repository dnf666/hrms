package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Builder;
import lombok.Data;

public interface ValidateMenuName {
    @Data
    class Arg {
        @JSONField(name = "M1")
        String name;
        @JSONField(name = "M2")
        @JsonProperty("menu_id")
        String menuId;
    }

    @Data
    class ApinameArg {
        @JSONField(name = "M1")
        String apiname;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private Boolean result;
    }
}

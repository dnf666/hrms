package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Builder;
import lombok.Data;

public interface CrmAdminEnableDisableArg {
    @Data
    class Arg {
        @JSONField(name = "M1")
        @JsonProperty("menu_id")
        private String menuId;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private boolean result = false;
    }
}

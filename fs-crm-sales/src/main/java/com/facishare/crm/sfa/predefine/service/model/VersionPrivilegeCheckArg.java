package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

public interface VersionPrivilegeCheckArg {
    @Data
    class Arg implements Serializable {
        @JsonProperty("api_names")
        @JSONField(name = "api_names")
        private Set<String> apiNames;

        @JsonProperty("action_codes")
        @JSONField(name = "action_codes")
        private Set<String> actionCodes;
    }
}

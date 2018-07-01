package com.facishare.crm.goal.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

public interface DisableRule {
    @Data
    class Arg {
        @JsonProperty("rule_id")
        String ruleId;
    }

    @Data
    @Builder
    class Result {
        @JsonProperty("isSuccess")
        Boolean success;
    }
}

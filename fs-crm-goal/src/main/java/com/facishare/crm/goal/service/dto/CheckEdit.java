package com.facishare.crm.goal.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public interface CheckEdit {
    @Data
    class Arg {
        @JsonProperty("rule_id")
        String ruleId;
    }

    @Data
    @Builder
    class Result {
        @JsonProperty("value")
        Boolean value;
    }
}

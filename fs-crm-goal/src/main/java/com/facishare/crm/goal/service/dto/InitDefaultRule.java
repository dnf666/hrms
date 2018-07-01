package com.facishare.crm.goal.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

public interface InitDefaultRule {
    @Data
    @Builder
    class Result {
        @JsonProperty("isSuccess")
        Boolean success;
    }
}

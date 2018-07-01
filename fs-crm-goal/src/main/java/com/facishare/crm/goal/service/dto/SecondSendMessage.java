package com.facishare.crm.goal.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public interface SecondSendMessage {

    @Data
    class Arg {
        @JsonProperty("rule_ids")
        List<String> ruleIds;
    }


    @Data
    @Builder
    class Result {
        @JsonProperty("isSuccess")
        Boolean success;
    }
}

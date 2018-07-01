package com.facishare.crm.goal.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public interface CreateOrUpdateRule {
    @Data
    class Arg {
        @JsonProperty("goal_rule")
        ObjectDataDocument goalRule;

        @JsonProperty("goal_rule_details")
        List<ObjectDataDocument> goalRuleDetails;

        @JsonProperty("check_circle_Ids")
        List<String> checkCircleIds;
    }

    @Data
    @Builder
    class Result {
        @JsonProperty("rule_id")
        String ruleId;
    }
}

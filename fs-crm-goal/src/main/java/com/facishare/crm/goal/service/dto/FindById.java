package com.facishare.crm.goal.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.INameCache;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FindById {

    @Data
    class Arg {
        @JsonProperty("rule_id")
        String ruleId;
    }

    @Data
    @Builder
    class Result {
        @JsonProperty("goal_rule")
        ObjectDataDocument goalRule;

        @JsonProperty("goal_rule_details")
        List<ObjectDataDocument> goalRuleDetails;

        @JsonProperty("check_circle_Ids")
        List<String> checkCircleIds;

        @JsonProperty("sub_goal_value_names")
        Map subGoalValueNames;

        @JsonProperty("deleted_options")
        Map deletedOptions;
    }
}

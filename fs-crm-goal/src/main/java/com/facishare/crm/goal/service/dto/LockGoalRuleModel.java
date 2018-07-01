package com.facishare.crm.goal.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by renlb on 2018/4/26.
 */
public interface LockGoalRuleModel {
    @Data
    class Arg{
        @JsonProperty("goal_rule_id")
        String goalRuleId;
        @JsonProperty("goal_rule_detail_id")
        String goalRuleDetailId;
        @JsonProperty("fiscal_year")
        String fiscalYear;
        Boolean lock;
    }

    @Data
    @Builder
    class Result{
        Boolean success;
        Boolean lockable;
        Boolean unlockable;
    }
}

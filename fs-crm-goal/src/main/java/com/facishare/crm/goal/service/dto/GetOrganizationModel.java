package com.facishare.crm.goal.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by renlb on 2018/4/23.
 */
public interface GetOrganizationModel {

    @Data
    class Arg {
        @JsonProperty("goal_rule_id")
        String goalRuleId;
        @JsonProperty("goal_rule_detail_id")
        String goalRuleDetailId;
        @JsonProperty("fiscal_year")
        String fiscalYear;
        @JsonProperty("goal_type")
        String goalType;
        @JsonProperty("check_object_id")
        String checkObjectId;
        String month;
        @JsonProperty("contain_goal_value")
        Boolean containGoalValue;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        List<OrganizationEntity> result;
        @JSONField(name = "M2")
        Boolean lockable;
        @JSONField(name = "M3")
        Boolean unlockable;
        @JSONField(name = "M4")
        Boolean allowPersonalModify;
    }

    @Data
    class OrganizationEntity {
        @JSONField(name = "M1")
        @JsonProperty("_id")
        String id;
        @JSONField(name = "M2")
        @JsonProperty("check_object_id")
        String checkObjectId;
        @JSONField(name = "M3")
        @JsonProperty("check_object_id__r")
        String checkObjectName;
        @JSONField(name = "M4")
        @JsonProperty("goal_type")
        String goalType;
        @JSONField(name = "M5")
        @JsonProperty("goal_value")
        String goalValue;
        @JSONField(name = "M6")
        @JsonProperty("parent_leaf")
        Boolean parentLeaf;
        @JSONField(name = "M7")
        @JsonProperty("parent_id")
        String parentId;
    }
}

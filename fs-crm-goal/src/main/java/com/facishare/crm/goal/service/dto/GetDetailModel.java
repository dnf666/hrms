package com.facishare.crm.goal.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by renlb on 2018/4/24.
 */
public interface GetDetailModel {
    @Data
    class Arg{
        @JsonProperty("goal_rule_id")
        @NotEmpty(message = "goalRuleId is blank")
        String goalRuleId;
        @JsonProperty("goal_rule_detail_id")
        String goalRuleDetailId;
        @JsonProperty("fiscal_year")
        @NotEmpty(message = "fiscalYear is blank")
        String fiscalYear;
        @JsonProperty("goal_type")
        @NotEmpty(message = "goalType is blank")
        String goalType;
        @JsonProperty("check_object_id")
        @NotEmpty(message = "checkObjectId is blank")
        String checkObjectId;
    }

    @Data
    @Builder
    class Result{
        @JSONField(name = "M1")
        ObjectDataDocument data;
        @JSONField(name = "M2")
        @JsonProperty("start_month")
        String startMonth;
        @JSONField(name = "M3")
        Boolean editable;
    }
}

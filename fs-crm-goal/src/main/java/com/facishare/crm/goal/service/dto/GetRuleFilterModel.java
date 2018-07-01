package com.facishare.crm.goal.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by renlb on 2018/4/4.
 */
public interface GetRuleFilterModel {
    @Data
    @Builder
    class Result{
        @JSONField(name = "M1")
        List<RuleFilterEntity> ruleFilterList;
    }

    @Data
    class RuleFilterEntity{
        @JSONField(name = "M1")
        String label;
        @JSONField(name = "M2")
        String value;
        @JSONField(name = "M3")
        @JsonProperty("fiscal_year")
        String fiscalYear;
        @JSONField(name = "M4")
        @JsonProperty("child_options")
        List<RuleFilterEntity> children;
    }
}

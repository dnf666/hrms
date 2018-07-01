package com.facishare.crm.goal.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by renlb on 2018/4/11.
 */
public interface CreateOrUpdateValue {

    @Data
    class Arg {
        @JSONField(name = "M1")
        @JsonProperty("goal_rule_id")
        String goalRuleId;
        @JSONField(name = "M2")
        @JsonProperty("goal_rule_detail_id")
        String goalRuleDetailId;
        @JSONField(name = "M3")
        @JsonProperty("fiscal_year")
        String fiscalYear;
        @JSONField(name = "M4")
        @JsonProperty("object_datas")
        List<ObjectDataDocument> objectDatas;
    }

    @Data
    @Builder
    class Result {
        List<ObjectDataDocument> details;
    }
}

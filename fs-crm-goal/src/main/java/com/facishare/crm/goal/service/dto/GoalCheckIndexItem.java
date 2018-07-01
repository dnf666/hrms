package com.facishare.crm.goal.service.dto;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 目标考核指标
 */
@Data
public class GoalCheckIndexItem {
    @JsonProperty("check_field_api_name")
    String checkFieldApiName;

    @JsonProperty("check_field_label")
    String checkFieldLabel;

    @JsonProperty("count_time_api_name")
    String countTimeApiName;

    @JsonProperty("count_time_label")
    String countTimeLabel;

    @JsonProperty("index_paraphrase")
    String indexParaphrase;

    @JsonProperty("check_field_aggregate_type")
    String checkFieldAggregateType;
}

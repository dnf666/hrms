package com.facishare.crm.goal.service.dto;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * 目标考核对象
 */
@Data
public class GoalCheckIndex {
    @JsonProperty("check_object_api_name")
    String checkObjectApiName;

    @JsonProperty("check_object_label")
    String checkObjectLabel;

    List<GoalCheckIndexItem> items;
}

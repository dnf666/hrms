package com.facishare.crm.goal.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface FindGoalCheckIndex {

    @Data
    @Builder
    class Result{
        Map dataList;
    }

}

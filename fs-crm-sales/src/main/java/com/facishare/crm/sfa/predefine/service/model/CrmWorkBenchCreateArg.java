package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

import lombok.Builder;
import lombok.Data;

public interface CrmWorkBenchCreateArg {
    @Data
    class Arg {
        @JSONField(name = "M1")
        @JsonProperty("data_list")
        private List<WorkBench> dataList;

        @JSONField(name = "M2")
        @JsonProperty("menu_id")
        private String menuId;
    }

    @Data
    class WorkBench {
        private String type;
        @JsonProperty("menu_item_id")
        private String menuItemId;
        @JsonProperty("is_hidden")
        private Boolean isHidden;
        private Integer number;
        @JsonProperty("menu_id")
        private String menuId;
        @JsonProperty("display_name")
        private String displayName;
        @JsonProperty("children")
        private List<WorkBench> children;
    }


    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private boolean result = false;
    }
}

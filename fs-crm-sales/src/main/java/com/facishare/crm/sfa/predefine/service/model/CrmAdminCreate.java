package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

import lombok.Builder;
import lombok.Data;

public interface CrmAdminCreate {
    @Data
    class Arg {
        @JSONField(name = "M1")
        @JsonProperty("object_data")
        ObjectDataDocument objectData;

        @JSONField(name = "M2")
        @JsonProperty("role_id_list")
        List<String> roleIdList;

        @JSONField(name = "M3")
        @JsonProperty("detail_menu_items")
        List<String> detailMenuItems;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private boolean result = false;
    }
}

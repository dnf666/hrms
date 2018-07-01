package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

import lombok.Builder;
import lombok.Data;

public interface CrmAdminView {
    @Data
    class Arg {
        @JSONField(name = "M1")
        @JsonProperty("menu_id")
        String menuId;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private ObjectDataDocument objectData;
        private ObjectDescribeDocument describe;
        @JSONField(name = "M3")
        Collection<String> roleIdList;

        @JSONField(name = "M4")
        Collection<String> detailMenuItems;
    }
}

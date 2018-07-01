package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Builder;
import lombok.Data;

public interface CrmMenuInitAddObject {
    @Data
    class Arg {
        private String tenantId;
        private String apiName;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private Boolean result;
    }
}

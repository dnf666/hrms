package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

public interface CrmMenuInitAddObjects {
    @Data
    class Arg {
        private String tenantIds;
        private String apiNames;
        private String afterApiName;
    }

    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private Map<String, InitSystemCrmArg.ResultModel> result;
    }
}

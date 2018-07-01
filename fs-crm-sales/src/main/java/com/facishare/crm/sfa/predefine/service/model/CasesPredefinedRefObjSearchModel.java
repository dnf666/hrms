package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/3/27.
 */
public interface CasesPredefinedRefObjSearchModel {
    @Data
    class Arg {
        @NotEmpty(message = "name is blank")
        private String name;

        @JsonProperty("api_name")
        @JSONField(name = "api_name")
        @NotEmpty(message = "api_name is blank")
        private String apiName;

        @JsonProperty("account_id")
        @JSONField(name = "account_id")
        private String accountId;
    }


    @Data
    @AllArgsConstructor
    class Result {
        @JSONField(name = "searchResult")
        private List<Map<String, Map<String, String>>> searchResult;
    }


}

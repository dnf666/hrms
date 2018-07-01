package com.facishare.crm.sfa.predefine.service.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

import lombok.Data;

public interface ImportPartnerAndOutInfoByName {
    @Data
    class Arg {
        @JsonProperty("row_partner_name")
        private List<importArg> importArgs;

    }

    @Data
    class importArg {
        String rowNo;
        String name;
    }

    @Data
    class PartnerOutInfo {
        private String id;
        private Integer outTenantId;
        private Long outOwner;
        private String name;
        private String errorMsg;
    }
}

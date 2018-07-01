package com.facishare.crm.rest.dto;

import lombok.Data;
import lombok.ToString;

public class SyncTenantSwitchModel {
    @Data
    public static class Arg {
        String key;
        String value;
    }

    @ToString
    @Data
    public static class Result {
        private Boolean value;
        private Boolean success;
        private String message;
        private Integer errorCode;
    }

}

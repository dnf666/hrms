package com.facishare.crm.rest.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

public class GetCurInstanceStateModel {
    @Data
    @ToString
    public static class Arg {
        List<String> objectIds;
    }

    @Data
    @ToString
    public static class Result {
        private int code;
        private String message;
        private List<IntanceStatus> data;

        public boolean success() {
            return code == 0;
        }
    }

    @Data
    @ToString
    public static class IntanceStatus {
        private String instanceId;
        private String objectId;
        private String status;
        private String triggerType;
    }
}

package com.facishare.crm.rest.dto;

import lombok.Data;

@Data
public class ApprovalInitModel {

    @Data
    public static class Arg {
        private String entityId;
    }

    @Data
    public static class Result {
        private int code;
        private String message;
        private boolean data;
    }
}

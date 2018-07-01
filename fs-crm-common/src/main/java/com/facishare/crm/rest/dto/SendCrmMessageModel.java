package com.facishare.crm.rest.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SendCrmMessageModel {

    @Data
    public static class Arg {
        private String employeeId;
        private Object content;
        private Integer remindRecordType;
        private String content2Id;
        private String dataId;
        private Set<Integer> receiverIds;
        private String title;
    }

    @Data
    public static class Result {
        private boolean value;
        private boolean success;
        private String message;
        private Integer errorCode;
    }
}

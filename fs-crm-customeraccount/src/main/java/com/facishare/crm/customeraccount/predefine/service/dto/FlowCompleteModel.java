package com.facishare.crm.customeraccount.predefine.service.dto;

import lombok.Data;
import lombok.ToString;

public class FlowCompleteModel {
    @Data
    @ToString
    public static class Arg {
        private String objectApiName;
        private String dataId;
        private String approvalType;
        private String lifeStatus;
    }

    @Data
    public static class Result {
        private boolean success;
    }
}

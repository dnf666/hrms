package com.facishare.crm.customeraccount.predefine.service.dto;

import lombok.Data;
import lombok.ToString;

public class SfaFlowCompleteModel {
    @Data
    @ToString
    public static class Arg {
        private String dataId;
        private String lifeStatus;
        private String approvalType;
    }

    @Data
    public static class Result {
        private boolean success;
    }
}

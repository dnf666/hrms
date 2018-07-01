package com.facishare.crm.customeraccount.predefine.service.dto;

import lombok.Data;

public class CommonFlowCompleteModel {

    @Data
    public static class Arg {
        private String objectApiName;
        private String dataId;
    }

    @Data
    public static class Result {
        private boolean success;
    }
}

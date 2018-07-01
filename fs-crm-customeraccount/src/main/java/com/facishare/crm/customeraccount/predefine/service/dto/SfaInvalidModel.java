package com.facishare.crm.customeraccount.predefine.service.dto;

import lombok.Data;

public class SfaInvalidModel {
    @Data
    public static class Arg {
        private String lifeStatus;
        private String dataId;
    }

    @Data
    public static class Result {
        private String success="ok";
    }
}

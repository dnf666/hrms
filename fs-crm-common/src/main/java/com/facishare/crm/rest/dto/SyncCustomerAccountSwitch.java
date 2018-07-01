package com.facishare.crm.rest.dto;

import lombok.Data;

/**
 * Created by xujf on 2017/10/17.
 */
@Data
public class SyncCustomerAccountSwitch {

    @Data
    public static class Arg {
        String key;
        String value;
    }

    @Data
    public static class Result {
        private boolean value;
        private boolean success;
        private String message;
        private Integer errorCode;
    }

}

package com.facishare.crm.customeraccount.predefine.service.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by xujf on 2017/12/4.
 */
public class SfaEditModel {

    @Data
    @ToString
    public static class Arg {
        private String dataId;
        private String lifeStatus;
    }

    @Data
    public static class Result {
        private boolean success;
    }
}

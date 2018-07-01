package com.facishare.crm.customeraccount.predefine.service.dto;

import com.facishare.paas.metadata.api.IObjectData;

import lombok.Data;

/**
 * Created by xujf on 2017/10/12.
 */
public class GetCustomerByIdModel {
    @Data
    public static class Arg {
        private String customerId;
    }

    @Data
    public static class Result {
        private IObjectData objectData;
    }

}

package com.facishare.crm.customeraccount.predefine.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

public class GetByPaymentIdModel {
    @Data
    public static class Arg {
        private String paymentId;
    }

    @Data
    public static class Result {
        private ObjectDataDocument objectData;
    }
}

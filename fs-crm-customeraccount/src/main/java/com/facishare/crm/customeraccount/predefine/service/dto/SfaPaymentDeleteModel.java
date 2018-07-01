package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import lombok.Data;

public class SfaPaymentDeleteModel {
    @Data
    public static class Arg {
        private List<String> paymentIds;
    }

    @Data
    public static class Result {
        private String success="ok";
    }
}

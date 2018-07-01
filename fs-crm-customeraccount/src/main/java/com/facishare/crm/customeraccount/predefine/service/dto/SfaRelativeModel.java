package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

public class SfaRelativeModel {
    @Data
    @ToString
    public static class Arg {
        private String paymentId;
    }

    @Data
    public static class Result {
        private String prepayName;
        private List<String> rebateOutcomeNames;
    }
}

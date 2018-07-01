package com.facishare.crm.customeraccount.predefine.service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

public class BatchGetRebateAmountModel {
    @Data
    public static class Arg {
        private List<String> orderPaymentIds;
    }

    @Data
    public static class Result {
        private Map<String, BigDecimal> orderPaymentIdRebateAmountMap;
    }
}

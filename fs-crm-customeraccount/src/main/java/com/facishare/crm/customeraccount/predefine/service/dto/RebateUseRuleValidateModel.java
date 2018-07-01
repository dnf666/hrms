package com.facishare.crm.customeraccount.predefine.service.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

public class RebateUseRuleValidateModel {

    @Data
    public static class Arg {
        private String customerId;
        private Map<String, BigDecimal> orderIdRebateAmountMap;
    }

    @Data
    public static class RebateOrderAmount {
        private BigDecimal rebateAmount;
        private BigDecimal orderAmount;
    }

    @Data
    public static class Result {
        private Map<String, RebateUseRuleValidateResult> orderIdValidateResultMap;
    }

    @Data
    public static class RebateUseRuleValidateResult {
        private Boolean hasRebateUseRule = Boolean.TRUE;
        private Boolean canUseRebate;
        private BigDecimal maxRebateAmountToUse;
    }
}

package com.facishare.crm.customeraccount.predefine.service.dto;

import java.math.BigDecimal;

import com.facishare.paas.metadata.api.IObjectData;

import lombok.Data;
import lombok.ToString;

@Data
public class RebateIncomeModle {

    @Data
    @ToString
    public static class PayForOutcomeModel {
        private BigDecimal payAmount;
        private IObjectData rebateIncomeObj;
    }
}

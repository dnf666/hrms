package com.facishare.crm.customeraccount.predefine.service.dto;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

public class SfaRefundCreateModel {
    @Data
    public static class Arg {
        private ObjectDataDocument prepayDetailData;
        private ObjectDataDocument rebateIncomeDetailData;
    }

    @Data
    public static class Result {
        private ObjectDataDocument prepayDetailData;
        private ObjectDataDocument rebateIncomeDetailData;
    }
}

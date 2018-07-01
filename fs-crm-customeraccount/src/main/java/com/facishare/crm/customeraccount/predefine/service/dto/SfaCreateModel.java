package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

public class SfaCreateModel {
    @Data
    public static class Arg {
        private ObjectDataDocument prepayDetailData;
        private ObjectDataDocument rebateOutcomeDetailData;
    }

    @Data
    public static class Result {
        private ObjectDataDocument prepayDetailData;
        private List<ObjectDataDocument> rebateOutcomeDetailDatas;
    }
}

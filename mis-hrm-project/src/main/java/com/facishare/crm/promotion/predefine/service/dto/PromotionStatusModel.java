package com.facishare.crm.promotion.predefine.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

public class PromotionStatusModel {
    @Data
    public static class Arg {
        private List<String> tenantIds;
    }

    @Data
    public static class Result {
        private Map<String, String> promotionStatus;
    }
}

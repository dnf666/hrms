package com.facishare.crm.promotion.predefine.service.dto;

import java.util.List;

import lombok.Data;

public class BatchGetProductQuotaByProductIdsModel {
    @Data
    public static class Arg {
        private List<PromotionProductIdArg> promotionProductIdArgs;
    }

    @Data
    public static class PromotionProductIdArg {
        private String promotionId;
        private String productId;
        private Double amount;
    }

    @Data
    public static class Result {
        private List<PromotionProductQuota> promotionProductQuotas;
    }

    @Data
    public static class PromotionProductQuota {
        private String promotionId;
        private String productId;
        private Integer quota;
        private Integer leftQuota;
        private Boolean sales;
    }
}

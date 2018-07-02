package com.facishare.crm.promotion.predefine.service.dto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

@Data
public class PromotionType {
    public enum PromotionSwitchEnum {
        NOT_OPEN(0, "未开启"), PROMOTION_FAIL(1, "初始化促销失败"), OPENED(2, "开启成功"), SALESORDER_FAIL(3, "同步初始化销售订单失败");
        public int status;
        public String message;

        PromotionSwitchEnum(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public static Optional<PromotionSwitchEnum> get(int status) {
            for (PromotionSwitchEnum promotionSwitchEnum : PromotionSwitchEnum.values()) {
                if (promotionSwitchEnum.status == status) {
                    return Optional.of(promotionSwitchEnum);
                }
            }
            return Optional.empty();
        }

    }

    @Data
    public static class EnableResult {
        private int enableStatus;
        private String message;
    }

    @Data
    public static class IsEnableResult {
        private Boolean enable;
    }

    @Data
    public static class IdModel {
        private String id;
    }

    @Data
    public static class IdsModel {
        private List<String> ids;
    }

    @Data
    public static class DetailResult {
        private ObjectDataDocument promotion;
        private List<ObjectDataDocument> promotionProducts;
        private List<ObjectDataDocument> promotionRules;
    }

    @Data
    public static class PromotionListResult {
        private List<ObjectDataDocument> promotions;
    }

    @Data
    public static class ProductPromotionListArg {
        private String customerId;
        private List<String> productIds;
    }

    @Data
    public static class ProductPromotionResult {
        private List<WrapProductPromotion> promotions;
    }

    @Data
    public static class ProductToPromotionId {
        private Map<String, List<String>> productToPromotionMap;

    }

    @Data
    public static class WrapProductPromotion {
        private String productId;
        private List<DetailResult> promotions;
    }

    @Data
    public static class PromotionRuleResult {
        private List<DetailResult> promotions;
    }

    @Data
    public static class CustomerIdArg {
        private String customerId;
    }

    @Data
    public static class ListProductsArg extends PageArg {
        private String customerId;
    }

    @Data
    public static class ListProductResult extends PageArg {
        private int totalNumber;
        private List<ObjectDataDocument> promotionProducts;
    }
}

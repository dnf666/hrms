package com.facishare.crm.stock.predefine.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class QueryByProductsIdModel {
    @Data
    public static class Warehouse {
        /**
         * 仓库ID
         */
        private String id;
        /**
         * 仓库名称
         */
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class ProductInfo {
        /**
         * 产品ID
         */
        private String productId;

        /**
         * 产品库存精确值
         */
        private BigDecimal accurateNum;

        /**
         * 产品库存模糊值
         */
        private String fuzzyDescription;
    }

    @Data
    public static class Arg {
        private String accountId;
        private List<String> productIds;
    }

    @Data
    public static class Result {
        private int isShowStock;
        private Warehouse warehouse;
        private List<ProductInfo> stocks;
    }
}

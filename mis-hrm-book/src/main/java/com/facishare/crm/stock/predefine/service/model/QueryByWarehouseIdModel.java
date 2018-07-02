package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

import java.math.BigDecimal;

public class QueryByWarehouseIdModel {
    @Data
    public static class Arg {
        private String productId;
        private String warehouseId;
    }

    @Data
    public static class Result {

        /**
         * 是否显示库存  1：不显示 2：显示
         */
        private Integer isShowStock;
        /**
         * 产品库存精确值
         */
        private BigDecimal accurateNum;

        /**
         * 产品库存模糊值
         */
        private String fuzzyDescription;
    }
}

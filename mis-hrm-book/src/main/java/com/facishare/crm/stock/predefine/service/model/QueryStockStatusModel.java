package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/10
 */
public class QueryStockStatusModel {
    @Data
    public static class Result {
        /**
         * 库存类型
         * @see com.facishare.crm.stock.enums.StockTypeEnum
         */
        private Integer stockType;
    }
}

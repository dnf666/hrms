package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author liangk
 */
public class SaveStockConfigModel {
    @Data
    public static class Arg {
        private String validateOrderType;
        private String stockViewType;
        private String orderWarehouseType;
        private String stockWarningType;
        private String isNotShowZeroStockType;
    }

    @Data
    public static class Result {
        private Boolean isSuccess;
    }
}

package com.facishare.crm.erpstock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/8
 */
public class SaveErpStockConfigModel {
    @Data
    public static class Arg {
        private String validateOrderType;
        private String isNotShowZeroStockType;
    }

    @Data
    public static class Result {
        private Boolean isSuccess = true;
    }
}

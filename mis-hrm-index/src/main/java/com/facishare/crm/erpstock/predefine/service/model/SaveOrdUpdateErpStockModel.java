package com.facishare.crm.erpstock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/9
 */
public class SaveOrdUpdateErpStockModel {
    @Data
    public static class Result {
        private boolean isSuccess;
        private String stockId;
    }
}

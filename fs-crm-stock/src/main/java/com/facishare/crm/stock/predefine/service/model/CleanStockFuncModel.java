package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/1
 */
public class CleanStockFuncModel {
    @Data
    public static class Result {
        private String result = "success";
    }

    @Data
    public static class Arg {
        private String tenantId;
    }
}

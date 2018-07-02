package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/2
 */
public class CloseStockSwitchModel {
    @Data
    public static class Arg {
        private String tenantId;
    }

    @Data
    public static class Result {
        private String result = "success";
    }
}
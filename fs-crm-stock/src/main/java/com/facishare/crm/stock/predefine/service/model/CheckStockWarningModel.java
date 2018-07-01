package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/26
 */
public class CheckStockWarningModel {
    @Data
    public static class Arg {
        String tenantId;
    }

    @Data
    public static class Result {
        String result = "success";
    }
}

package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/9
 */
public class IsStockEnableModel {
    @Data
    public static class ResultVO {
        private int errCode;
        private String errMessage;
        private Result result;
    }

    @Data
    public static class Result {
        private Boolean isEnable;
    }
}

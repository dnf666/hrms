package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/30
 */
public class ModifyStockModel {
    @Data
    public static class Arg {
        private String tenantId;
        private String stockId;
        private String modifyBlockedStock;
        private String modifyRealStock;
    }

    @Data
    public static class Result {
        private String result = "success";
    }
}

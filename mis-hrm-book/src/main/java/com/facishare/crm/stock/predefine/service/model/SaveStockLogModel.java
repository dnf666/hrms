package com.facishare.crm.stock.predefine.service.model;

import com.facishare.crm.stock.model.StockLogDO;
import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/31
 */
public class SaveStockLogModel {
    @Data
    public static class Arg {
        private StockLogDO stockLogDO;
    }

    @Data
    public static class Result {
        String result = "success";
    }
}

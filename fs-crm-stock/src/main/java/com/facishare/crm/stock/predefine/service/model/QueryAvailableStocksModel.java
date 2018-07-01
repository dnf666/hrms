package com.facishare.crm.stock.predefine.service.model;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 09/03/2018
 */
@Data
@ToString
public class QueryAvailableStocksModel {
    @Data
    @ToString
    public static class Arg {
        private String customerId;
        private String warehouseId;
        private List<String> productIds;
    }

    @Data
    @ToString
    public static class Result {
        //无意义，满足.net的需求
        private String info = "info";

        private Map<String, BigDecimal> data = Maps.newHashMap();
    }
}

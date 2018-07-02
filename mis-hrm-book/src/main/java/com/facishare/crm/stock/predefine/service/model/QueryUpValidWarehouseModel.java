package com.facishare.crm.stock.predefine.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by linchf on 2018/1/18.
 */
public class QueryUpValidWarehouseModel {
    @Data
    public static class Arg {
        private String accountId;
    }

    @Data
    @AllArgsConstructor
    public static class Result {
        private Map<String, Object> result;
    }
}

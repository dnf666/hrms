package com.facishare.crm.stock.predefine.service.model;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 26/01/2018
 */
public class QueryDownValidByIdsModel {
    @Data
    public static class Result {
        //Todo 增加errorCode和errorMessage
        private int orderWarehouseType;
        private Map<String, List<WarehouseVO>> data = Maps.newHashMap();
    }

    @Data
    public static class Arg {
        private String accountId;
        private List<String> productIds;
    }
}

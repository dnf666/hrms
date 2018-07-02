package com.facishare.crm.stock.predefine.service.model;


import lombok.Data;
import java.util.List;

/**
 * @author liangk 2018/01/09
 *
 */
public class WareHouseDetailModel {
    @Data
    public static class Result {
        private List<WarehouseVO> warehouses;
    }

    @Data
    public static class Arg {
        private String accountId;
        private String productId;
    }
}

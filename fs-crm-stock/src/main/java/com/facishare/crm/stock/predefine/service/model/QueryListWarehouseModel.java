package com.facishare.crm.stock.predefine.service.model;

import com.facishare.crm.stock.model.SimpleWarehouseVO;
import lombok.Data;

import java.util.List;

/**
 * @author linchf
 * @date 2018/4/4
 */
public class QueryListWarehouseModel {
    @Data
    public static class Result {
        List<SimpleWarehouseVO> warehouseVOs;
    }
}

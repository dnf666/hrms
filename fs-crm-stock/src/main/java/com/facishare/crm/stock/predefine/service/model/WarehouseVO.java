package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liangk
 * @date 27/01/2018
 */
@Data
public class WarehouseVO {
    private String id;
    private String name;
    private BigDecimal accurateNum;
    private String fuzzyDescription;
    private Boolean isDefaultWarehouse;
}

package com.facishare.crm.stock.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linchf
 * @date 2018/4/4
 */
@Data
@Builder
public class SimpleWarehouseVO implements Serializable {

    private static final long serialVersionUID = 8912064275862138303L;

    private String id;
    private String name;
}

package com.facishare.crm.stock.predefine.service.model;

import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.predefine.service.dto.OrderCheckType;
import com.facishare.crm.stock.predefine.service.dto.OrderWarehouseType;
import com.facishare.crm.stock.predefine.service.dto.StockViewType;
import com.facishare.crm.stock.predefine.service.dto.StockWarningType;
import lombok.Data;

/**
 * Created by linchf on 2018/1/12.
 */
public class QueryStockConfigModel {
    @Data
    public static class Result {
        private boolean enable = false;
        private String validateOrderType = OrderCheckType.OrderCheckTypeEnum.CANNOTSUBMIT.getStringStatus();
        private String stockViewType = StockViewType.StockViewTypeEnum.NO.getStringStatus();
        private String orderWarehouseType = OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE.getStringStatus();
        private String stockWarningType = StockWarningType.StockWarningTypeEnum.UNABLE.getStringStatus();
        private String isNotShowZeroStockType = YesOrNoEnum.NO.getStringStatus();
    }
}

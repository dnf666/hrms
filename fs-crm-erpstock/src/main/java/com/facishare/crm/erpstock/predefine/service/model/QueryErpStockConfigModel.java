package com.facishare.crm.erpstock.predefine.service.model;

import com.facishare.crm.erpstock.enums.YesOrNoEnum;
import com.facishare.crm.erpstock.predefine.service.dto.ErpOrderCheckType;
import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/8
 */
public class QueryErpStockConfigModel {
    @Data
    public static class Result {
        private boolean enable = false;
        private String validateOrderType = ErpOrderCheckType.OrderCheckTypeEnum.CANNOTSUBMIT.getStringStatus();
        private String isNotShowZeroStockType = YesOrNoEnum.NO.getStringStatus();
    }
}

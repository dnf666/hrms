package com.facishare.crm.promotion.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

public enum PromotionErrorCode implements ErrorCode {
    QUERY_ROLE_ERROR(201001, "角色查询异常"),

    QUERY_SALES_ORDER_ERROR(201002, "查询订单关联的促销异常"),

    CONFIG_ERROR(201003, "Config服务异常"),

    QUERY_PROMOTION_QUANTITY(201004, "查询已参数促销的数量异常");

    private String message;
    private int errorCode;

    PromotionErrorCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public int getCode() {
        return errorCode;
    }
}

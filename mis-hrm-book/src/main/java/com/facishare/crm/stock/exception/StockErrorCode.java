package com.facishare.crm.stock.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/10/11.
 */
public enum StockErrorCode implements ErrorCode {
    OK(0, "成功"),
    BUSINESS_ERROR(1000, "业务异常"),
    INIT_ERROR(1001, "库存描述初始化异常"),
    DISPLAY_NAME_EXIST(1002, "对象名称已存在"),
    DELIVER_WAREHOUSE_NOT_EXIST(1003, "订单无发货仓库"),
    STOCK_INSUFFICIENT(1004, "库存不足"),
    STOCK_NOT_ENABLE(1005, "未启用库存"),
    DEFAULT_WAREHOUSE_NOT_EXIST(1006, "未设置默认仓库"),
    WAREHOUSE_NOT_EXIST(1007, "未设置默认仓库和适用仓库"),
    WAREHOUSE_UN_SATISFIED(1008, "仓库不适用于客户");


    int code;
    String message;

    StockErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    StockErrorCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStringCode() {
        return String.valueOf(code);
    }
}

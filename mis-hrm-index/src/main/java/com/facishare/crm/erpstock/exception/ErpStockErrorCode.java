package com.facishare.crm.erpstock.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/10/11.
 */
public enum ErpStockErrorCode implements ErrorCode {
    OK(0, "成功"),
    BUSINESS_ERROR(1000, "业务异常"),
    INIT_ERROR(1001, "ERP库存描述初始化异常");

    int code;
    String message;

    ErpStockErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ErpStockErrorCode(int code) {
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

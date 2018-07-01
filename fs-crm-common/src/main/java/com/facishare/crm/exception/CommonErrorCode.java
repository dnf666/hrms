package com.facishare.crm.exception;


import com.facishare.paas.appframework.core.exception.ErrorCode;

public enum  CommonErrorCode implements ErrorCode {
    REPEATED_INVOKE(1000, "重复调用");

    int code;
    String message;

    CommonErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

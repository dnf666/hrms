package com.facishare.crm.sfa.predefine.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

public enum SFAErrorCode implements ErrorCode {
    CLIENT_UPGRADE_PROMPT(0,"有新功能上线啦，请先升级客户端!");


    int code;
    String message;

    SFAErrorCode(int code, String message) {
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

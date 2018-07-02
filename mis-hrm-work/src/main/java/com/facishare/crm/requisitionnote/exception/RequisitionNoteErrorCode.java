package com.facishare.crm.requisitionnote.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * @author liangk
 * @date 13/03/2018
 */
public enum RequisitionNoteErrorCode implements ErrorCode {
    OK(0, "成功"),
    BUSINESS_ERROR(1000, "业务异常"),
    INIT_ERROR(1001, "调拨单描述初始化异常");

    int code;
    String message;

    RequisitionNoteErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    RequisitionNoteErrorCode(int code) {
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

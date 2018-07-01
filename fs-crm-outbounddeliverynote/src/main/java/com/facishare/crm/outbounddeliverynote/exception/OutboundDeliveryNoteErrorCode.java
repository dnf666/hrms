package com.facishare.crm.outbounddeliverynote.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * @author linchf
 * @date 2018/3/14
 */
public enum OutboundDeliveryNoteErrorCode implements ErrorCode {
    OK(0, "成功"),
    BUSINESS_ERROR(1000, "业务异常"),
    INIT_ERROR(1001, "出库单描述初始化异常");


    int code;
    String message;

    OutboundDeliveryNoteErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    OutboundDeliveryNoteErrorCode(int code) {
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

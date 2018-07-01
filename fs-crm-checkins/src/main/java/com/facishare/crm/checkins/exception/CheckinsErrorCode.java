package com.facishare.crm.checkins.exception;

import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
public enum CheckinsErrorCode implements ErrorCode {
    GO_TO_WAIQIN(2018,"外勤预制对象不支持此操作"),
    REPEAT_INSERT(2019,"data_Id"),
    GO_TO_630_WAIQIN(2020,"请使用6.3版本以上外勤，执行该操作");


    int code;
    String message;

    CheckinsErrorCode(int code, String message) {
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

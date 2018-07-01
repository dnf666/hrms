package com.facishare.crm.checkins.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
public class CheckinsException extends AppBusinessException {
    public CheckinsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}

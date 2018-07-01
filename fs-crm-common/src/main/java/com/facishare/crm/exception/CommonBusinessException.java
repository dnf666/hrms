package com.facishare.crm.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class CommonBusinessException extends AppBusinessException {

    private static final long serialVersionUID = 7343995120374034786L;

    public CommonBusinessException(CommonErrorCode commonErrorCode) {
        super(commonErrorCode.getMessage(), commonErrorCode);
    }

    public CommonBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
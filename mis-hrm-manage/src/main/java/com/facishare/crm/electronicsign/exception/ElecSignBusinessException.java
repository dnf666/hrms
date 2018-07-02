package com.facishare.crm.electronicsign.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

public class ElecSignBusinessException extends AppBusinessException {
    private static final long serialVersionUID = -2625511277848817483L;

    public ElecSignBusinessException(ElecSignErrorCode elecSignErrorCode) {
        super(elecSignErrorCode.getMessage(), elecSignErrorCode);
    }

    public ElecSignBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
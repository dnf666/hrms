package com.facishare.crm.customeraccount.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class CustomerAccountBusinessException extends AppBusinessException {
    private static final long serialVersionUID = 8135898467612733056L;

    public CustomerAccountBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
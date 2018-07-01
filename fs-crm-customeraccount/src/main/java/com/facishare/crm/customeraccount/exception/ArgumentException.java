package com.facishare.crm.customeraccount.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.AppFrameworkErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class ArgumentException extends AppBusinessException {
    private static final long serialVersionUID = 8135898467612733056L;
    private String argument;

    public ArgumentException(String argument, String message) {
        super(message, AppFrameworkErrorCode.VALIDATION_ERROR);
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
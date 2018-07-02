package com.facishare.crm.stock.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class StockBusinessException extends AppBusinessException {
    private static final long serialVersionUID = 8135898467612733056L;

    public StockBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public StockBusinessException(StockErrorCode stockErrorCode) {
        super(stockErrorCode.getMessage(), stockErrorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
package com.facishare.crm.erpstock.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class ErpStockBusinessException extends AppBusinessException {


    public ErpStockBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public ErpStockBusinessException(ErpStockErrorCode stockErrorCode) {
        super(stockErrorCode.getMessage(), stockErrorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
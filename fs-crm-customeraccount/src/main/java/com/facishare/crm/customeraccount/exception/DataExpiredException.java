package com.facishare.crm.customeraccount.exception;

import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;

public class DataExpiredException extends MetaDataBusinessException {
    public DataExpiredException(String message) {
        super(message);
    }

    //数据更新版本过期包装异常，retry机制细分异常
    public DataExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}

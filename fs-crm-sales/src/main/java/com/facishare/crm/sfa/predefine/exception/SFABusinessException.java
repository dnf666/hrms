package com.facishare.crm.sfa.predefine.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

public class SFABusinessException extends AppBusinessException {

    public SFABusinessException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}

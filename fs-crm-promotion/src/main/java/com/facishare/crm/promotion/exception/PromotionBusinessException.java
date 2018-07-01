package com.facishare.crm.promotion.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

public class PromotionBusinessException extends AppBusinessException {

    public PromotionBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

}

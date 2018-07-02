package com.facishare.crm.deliverynote.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * Created by xujf on 2017/9/28.
 */
public class DeliveryNoteBusinessException extends AppBusinessException {
    private static final long serialVersionUID = -1683360002912396844L;

    public DeliveryNoteBusinessException(DeliveryNoteErrorCode deliveryNoteErrorCode) {
        super(deliveryNoteErrorCode.getMessage(), deliveryNoteErrorCode);
    }

    public DeliveryNoteBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
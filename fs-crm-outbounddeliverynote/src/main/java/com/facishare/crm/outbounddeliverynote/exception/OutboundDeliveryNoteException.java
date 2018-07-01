package com.facishare.crm.outbounddeliverynote.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * @author linchf
 * @date 2018/3/14
 */
public class OutboundDeliveryNoteException extends AppBusinessException {

    private static final long serialVersionUID = 5428332111407105147L;

    public OutboundDeliveryNoteException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode outboundDeliveryNoteErrorCode) {
        super(outboundDeliveryNoteErrorCode.getMessage(), outboundDeliveryNoteErrorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
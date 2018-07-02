package com.facishare.crm.requisitionnote.exception;

import com.facishare.paas.appframework.core.exception.AppBusinessException;
import com.facishare.paas.appframework.core.exception.ErrorCode;

/**
 * @author liangk
 * @date 13/03/2018
 */
public class RequisitionNoteBusinessException extends AppBusinessException{

    public RequisitionNoteBusinessException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public RequisitionNoteBusinessException(RequisitionNoteErrorCode requisitionNoteErrorCode) {
        super(requisitionNoteErrorCode.getMessage(), requisitionNoteErrorCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}

package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;

public class DeliveryNoteBulkRecoverAction extends StandardBulkRecoverAction {
    @Override
    public void before(Arg arg) {
        // 发货单不可恢复
        throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.INVALID_STATUS_NOT_ALLOW_RECOVER);
    }
}

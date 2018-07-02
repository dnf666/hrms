package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeliveryNoteProductAddAction extends CommonEditAction {
    @Override
    protected void before(Arg arg) {
        // 发货单产品不可新增
        throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_UNABLE_TO_ADD);
    }
}

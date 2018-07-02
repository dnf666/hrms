package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.paas.metadata.util.SpringUtil;

public class DeliveryNoteFullDetailController extends DeliveryNoteDetailController {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext()
            .getBean(DeliveryNoteManager.class);

    @Override
    protected void before(Arg arg) {
        arg.setFromRecycleBin(true);
        super.before(arg);
    }

    @Override
    protected Result doService(Arg arg) {
        Result r = super.doService(arg);
        if (null != r && null != r.getData()) {
            r.setData(deliveryNoteManager.fillWithDetails(controllerContext.getRequestContext(),
                    controllerContext.getObjectApiName(), r.getData()));
        }
        return r;
    }
}

package com.facishare.crm.outbounddeliverynote.predefine.service;

import com.facishare.crm.outbounddeliverynote.predefine.service.dto.OutboundDeliveryNoteType;
import com.facishare.crm.outbounddeliverynote.predefine.service.model.CommonModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * @author linchf
 * @date 2018/3/20
 */
@ServiceModule("outbound_delivery_note")
public interface OutboundDeliveryNoteService {
    /**
     * 开启出库单
     */
    @ServiceMethod("enable_outbound_delivery_note")
    OutboundDeliveryNoteType.EnableOutboundDeliveryNoteResult enableOutboundDeliveryNote(ServiceContext serviceContext);

    /**
     * 刷库程序 对齐发货单出库单权限
     * @param serviceContext
     * @return
     */
    @ServiceMethod("addFuncAccess")
    CommonModel.Result addFuncAccess(ServiceContext serviceContext);
}

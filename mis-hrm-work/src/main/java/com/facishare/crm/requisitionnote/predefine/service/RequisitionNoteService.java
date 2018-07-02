package com.facishare.crm.requisitionnote.predefine.service;

import com.facishare.crm.requisitionnote.predefine.service.dto.RequisitionNoteType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * @author liangk
 * @date 14/03/2018
 */
@ServiceModule("requisition_note")
public interface RequisitionNoteService {

    /**
     * 开启调拨单
     */
    @ServiceMethod("enable_requisition")
    RequisitionNoteType.EnableRequisitionResult enableRequisition(ServiceContext serviceContext);

    /**
     * 是否确认入库
     */
    @ServiceMethod("is_confirmed")
    RequisitionNoteType.IsConfirmedResult isConfirmed(ServiceContext serviceContext, RequisitionNoteType.IsConfirmedArg arg);

    @ServiceMethod("add_field_and_data")
    RequisitionNoteType.AddFieldAndDataResult addFieldAndData(ServiceContext serviceContext);
}

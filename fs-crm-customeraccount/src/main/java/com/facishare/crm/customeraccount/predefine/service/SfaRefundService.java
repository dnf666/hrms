package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("refund")
public interface SfaRefundService {
    @ServiceMethod("create")
    SfaRefundCreateModel.Result create(SfaRefundCreateModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("invalid")
    SfaInvalidModel.Result invalid(SfaInvalidModel.Arg sfaInvalidArg, ServiceContext serviceContext);

    @ServiceMethod("bulk_invalid")
    SfaBulkInvalidModel.Result bulkInvalid(SfaBulkInvalidModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("flow_complete")
    SfaFlowCompleteModel.Result flowComplete(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_recover")
    SfaRefundRecoverModel.Result bulkRecover(SfaRefundRecoverModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_delete")
    SfaRefundDeleteModel.Result bulkDelete(SfaRefundDeleteModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("edit")
    SfaEditModel.Result edit(SfaEditModel.Arg arg, ServiceContext serviceContext);
}

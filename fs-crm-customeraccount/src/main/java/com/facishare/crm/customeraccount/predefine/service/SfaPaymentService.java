package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("payment")
public interface SfaPaymentService {
    @ServiceMethod("create")
    SfaCreateModel.Result create(SfaCreateModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("flow_complete")
    SfaFlowCompleteModel.Result flowComplete(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("invalid")
    SfaInvalidModel.Result invalid(SfaInvalidModel.Arg sfaInvalidArg, ServiceContext serviceContext);

    @ServiceMethod("bulk_invalid")
    SfaBulkInvalidModel.Result bulkInvalid(SfaBulkInvalidModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_recover")
    SfaPaymentRecoverModel.Result bulkRecover(SfaPaymentRecoverModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_delete")
    SfaPaymentDeleteModel.Result bulkDelete(SfaPaymentDeleteModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("edit")
    SfaEditModel.Result edit(SfaEditModel.Arg arg, ServiceContext serviceContext);

    //根据paymentId查询关联的预存款和支出编号
    @ServiceMethod("get_relative_names_by_order_payment_id")
    SfaRelativeModel.Result getRelativeNamesByPaymentId(SfaRelativeModel.Arg arg, ServiceContext serviceContext);

}

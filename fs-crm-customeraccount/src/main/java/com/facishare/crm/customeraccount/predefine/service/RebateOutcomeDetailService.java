package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.jaxrs.annotation.InnerAPI;

@InnerAPI
@ServiceModule("rebate_outcome_detail")
public interface RebateOutcomeDetailService {

    @ServiceMethod("list_by_rebate_income_id")
    ListByIdModel.Result listByRebateIncomeId(ListByIdModel.RebateOutcomeArg arg, ServiceContext serviceContext);
}

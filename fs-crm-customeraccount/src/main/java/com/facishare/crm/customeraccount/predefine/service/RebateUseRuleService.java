package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("rebate_use_rule")
public interface RebateUseRuleService {

    @ServiceMethod("validate")
    RebateUseRuleValidateModel.Result validate(RebateUseRuleValidateModel.Arg arg, ServiceContext serviceContext);
}

package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.customeraccount.predefine.manager.RebateUseRuleManager;
import com.facishare.crm.customeraccount.predefine.service.RebateUseRuleService;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.paas.appframework.core.model.ServiceContext;

@Service
public class RebateUseRuleServiceImpl implements RebateUseRuleService {
    @Autowired
    private RebateUseRuleManager rebateUseRuleManager;

    @Override
    public RebateUseRuleValidateModel.Result validate(RebateUseRuleValidateModel.Arg arg, ServiceContext serviceContext) {
        RebateUseRuleValidateModel.Result result = new RebateUseRuleValidateModel.Result();
        Map<String, RebateUseRuleValidateModel.RebateUseRuleValidateResult> orderValidateResultMap = rebateUseRuleManager.validate(serviceContext.getUser(), arg.getCustomerId(), arg.getOrderIdRebateAmountMap());
        result.setOrderIdValidateResultMap(orderValidateResultMap);
        return result;
    }

}

package com.facishare.crm.customeraccount.predefine;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

public enum CustomerAccountPredefineObject implements PreDefineObject {
    CustomerAccount(CustomerAccountConstants.API_NAME),

    PrepayDetail(PrepayDetailConstants.API_NAME),

    RebateIncomeDetail(RebateIncomeDetailConstants.API_NAME),

    RebateOutcomeDetail(RebateOutcomeDetailConstants.API_NAME),

    RebateUseRule(RebateUseRuleConstants.API_NAME);
    private String apiName;
    private static String PACKAGE_NAME = CustomerAccountPredefineObject.class.getPackage().getName();

    CustomerAccountPredefineObject(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getApiName() {
        return apiName;
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public ActionClassInfo getDefaultActionClassInfo(String actionCode) {
        String className = PACKAGE_NAME + ".action." + this + actionCode + "Action";
        return new ActionClassInfo(className);
    }

    @Override
    public ControllerClassInfo getControllerClassInfo(String methodName) {
        String className = PACKAGE_NAME + ".controller." + this + methodName + "Controller";
        return new ControllerClassInfo(className);
    }

    public static void init() {
        for (CustomerAccountPredefineObject object : CustomerAccountPredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

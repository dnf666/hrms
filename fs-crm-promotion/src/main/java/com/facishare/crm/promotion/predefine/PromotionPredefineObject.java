package com.facishare.crm.promotion.predefine;

import com.facishare.crm.promotion.constants.AdvertisementConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

public enum PromotionPredefineObject implements PreDefineObject {
    Promotion(PromotionConstants.API_NAME),

    PromotionProduct(PromotionProductConstants.API_NAME),

    PromotionRule(PromotionRuleConstants.API_NAME),

    Advertisement(AdvertisementConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = PromotionPredefineObject.class.getPackage().getName();

    PromotionPredefineObject(String apiName) {
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
        for (PromotionPredefineObject object : PromotionPredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

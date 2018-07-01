package com.facishare.crm.deliverynote.predefine;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

/**
 * Created by chenzs on 2018/1/8.
 */
public enum DeliveryNotePredefineObject implements PreDefineObject {
    DeliveryNote(DeliveryNoteObjConstants.API_NAME),
    DeliveryNoteProduct(DeliveryNoteProductObjConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = DeliveryNotePredefineObject.class.getPackage().getName();

    DeliveryNotePredefineObject(String apiName) {
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
        for (DeliveryNotePredefineObject object : DeliveryNotePredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

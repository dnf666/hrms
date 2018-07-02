package com.facishare.crm.outbounddeliverynote.predefine;

import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

/**
 * Created by linchf on 2018/1/9.
 */
public enum OutboundDeliveryNotePredefineObject implements PreDefineObject {
    OutboundDeliveryNote(OutboundDeliveryNoteConstants.API_NAME),
    OutboundDeliveryNoteProduct(OutboundDeliveryNoteProductConstants.API_NAME);


    private String apiName;
    private static String PACKAGE_NAME = OutboundDeliveryNotePredefineObject.class.getPackage().getName();

    OutboundDeliveryNotePredefineObject(String apiName) {
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
        for (OutboundDeliveryNotePredefineObject object : OutboundDeliveryNotePredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

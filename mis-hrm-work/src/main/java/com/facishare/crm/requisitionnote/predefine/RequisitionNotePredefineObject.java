
package com.facishare.crm.requisitionnote.predefine;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;


/**
 * @author liangk
 * @date 12/03/2018
 */
public enum RequisitionNotePredefineObject implements PreDefineObject {
    RequisitionNote(RequisitionNoteConstants.API_NAME),
    RequisitionNoteProduct(RequisitionNoteProductConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = RequisitionNotePredefineObject.class.getPackage().getName();

    RequisitionNotePredefineObject(String apiName) {
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
        for (RequisitionNotePredefineObject object : RequisitionNotePredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}


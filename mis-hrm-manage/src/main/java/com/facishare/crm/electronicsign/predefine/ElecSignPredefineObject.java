package com.facishare.crm.electronicsign.predefine;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

/**
 * Created by chenzs on 2018/4/16.
 */
public enum ElecSignPredefineObject implements PreDefineObject {
    InternalSignCertify(InternalSignCertifyObjConstants.API_NAME),
    AccountSignCertify(AccountSignCertifyObjConstants.API_NAME),
    SignRecord(SignRecordObjConstants.API_NAME),
    Signer(SignerObjConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = ElecSignPredefineObject.class.getPackage().getName();

    ElecSignPredefineObject(String apiName) {
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
        for (ElecSignPredefineObject object : ElecSignPredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}
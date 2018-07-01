package com.facishare.crm.erpstock.predefine;

import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.constants.ErpWarehouseConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

/**
 * Created by linchf on 2018/1/9.
 */
public enum ErpStockPredefineObject implements PreDefineObject {
    ErpWarehouse(ErpWarehouseConstants.API_NAME),
    ErpStock(ErpStockConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = ErpStockPredefineObject.class.getPackage().getName();

    ErpStockPredefineObject(String apiName) {
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
        for (ErpStockPredefineObject object : ErpStockPredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

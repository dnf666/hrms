package com.facishare.crm.stock.predefine;

import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

/**
 * Created by linchf on 2018/1/9.
 */
public enum StockPredefineObject implements PreDefineObject {
    WareHouse(WarehouseConstants.API_NAME),
    Stock(StockConstants.API_NAME),
    GoodsReceivedNote(GoodsReceivedNoteConstants.API_NAME),
    GoodsReceivedNoteProduct(GoodsReceivedNoteProductConstants.API_NAME);

    private String apiName;
    private static String PACKAGE_NAME = StockPredefineObject.class.getPackage().getName();

    StockPredefineObject(String apiName) {
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
        for (StockPredefineObject object : StockPredefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
}

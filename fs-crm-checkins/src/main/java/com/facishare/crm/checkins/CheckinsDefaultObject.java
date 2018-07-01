package com.facishare.crm.checkins;

import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangsm on 2018/4/8/0008.
 */
@Slf4j
public enum CheckinsDefaultObject implements PreDefineObject {
    CHECKINS_IMG("CheckinsImg", "CheckinsImgObj"),
    CHECKINS("Checkins", "CheckinsObj");
    private final String apiName;
    private final String name;
    private static final String PACKAGE_NAME = "com.facishare.crm.checkins";
    private static final String PACKAGE_NAME_ACTION = PACKAGE_NAME + ".action.";
    private static final String PACKAGE_NAME_CONTROLLER = PACKAGE_NAME + ".controller.";
    private static final String SUFFIX_ACTION = "Action";
    private static final String SUFFIX_CONTROLLER = "Controller";
    CheckinsDefaultObject(String name, String apiName) {
        this.name = name;
        this.apiName = apiName;
    }
    @Override
    public String getApiName() {
        return apiName;
    }
    public static void init() {
        for (CheckinsDefaultObject object : CheckinsDefaultObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }
    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public ActionClassInfo getDefaultActionClassInfo(String methodName) {
        log.info("getDefaultActionClassInfo methodName -{}",methodName);
        return new ActionClassInfo(generateClassName(PACKAGE_NAME_ACTION, methodName, SUFFIX_ACTION));
    }

    @Override
    public ControllerClassInfo getControllerClassInfo(String methodName) {
        log.info("getControllerClassInfo methodName -{}",methodName);
        return new ControllerClassInfo(
                generateClassName(PACKAGE_NAME_CONTROLLER, methodName, SUFFIX_CONTROLLER));
    }
    private String generateClassName(String packageName, String methodName, String suffix) {
        return packageName + name + methodName + suffix;
    }
}

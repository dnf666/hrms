package com.facishare.crm.goal;

import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

public enum GoalObject implements PreDefineObject {
    GOAL_RULE("GoalRule", "GoalRuleObj"),
    GOAL_RULE_DETAIL("GoalRuleDetail", "GoalRuleDetailObj"),
    GOAL_RULE_APPLY_CIRCLE("GoalRuleApplyCircle", "GoalRuleApplyCircleObj"),
    GOAL_VALUE("GoalValue", "GoalValueObj");

    private static final String PACKAGE_NAME = "com.facishare.crm.goal";
    private static final String PACKAGE_NAME_ACTION = PACKAGE_NAME + ".action.";
    private static final String PACKAGE_NAME_CONTROLLER = PACKAGE_NAME + ".controller.";
    private static final String SUFFIX_ACTION = "Action";
    private static final String SUFFIX_CONTROLLER = "Controller";

    private final String apiName;
    private final String name;

    GoalObject(String name, String apiName) {
        this.name = name;
        this.apiName = apiName;
    }

    public static void init() {
        for (GoalObject object : GoalObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
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
    public ActionClassInfo getDefaultActionClassInfo(String methodName) {
        return new ActionClassInfo(generateClassName(PACKAGE_NAME_ACTION, methodName, SUFFIX_ACTION));
    }

    @Override
    public ControllerClassInfo getControllerClassInfo(String methodName) {
        return new ControllerClassInfo(
                generateClassName(PACKAGE_NAME_CONTROLLER, methodName, SUFFIX_CONTROLLER));
    }

    private String generateClassName(String packageName, String methodName, String suffix) {
        return packageName + name + methodName + suffix;
    }
}
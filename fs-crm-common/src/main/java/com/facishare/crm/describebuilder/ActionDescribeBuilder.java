package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.ActionDescribe;

public class ActionDescribeBuilder {
    private ActionDescribe actionDescribe;

    private ActionDescribeBuilder() {
        actionDescribe = new ActionDescribe();
    }

    public static ActionDescribeBuilder builder() {
        return new ActionDescribeBuilder();
    }

    public ActionDescribe build() {
        return actionDescribe;
    }

    public ActionDescribeBuilder actionCode(String actionCode) {
        actionDescribe.setActionCode(actionCode);
        return this;
    }

    public ActionDescribeBuilder sourceType(String sourceType) {
        actionDescribe.setSourceType(sourceType);
        return this;
    }

    public ActionDescribeBuilder label(String label) {
        actionDescribe.setLabel(label);
        return this;
    }

    public ActionDescribeBuilder actionClass(String actionClass) {
        actionDescribe.setActionClass(actionClass);
        return this;
    }
}

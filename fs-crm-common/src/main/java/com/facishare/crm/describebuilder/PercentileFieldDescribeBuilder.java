package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.PercentileFieldDescribe;

public class PercentileFieldDescribeBuilder {
    private PercentileFieldDescribe percentileFieldDescribe;

    private PercentileFieldDescribeBuilder() {
        percentileFieldDescribe = new PercentileFieldDescribe();
        percentileFieldDescribe.setActive(true);
        percentileFieldDescribe.setDefineType("package");
        percentileFieldDescribe.setIsExtend(false);
        percentileFieldDescribe.setCreateTime(System.currentTimeMillis());
        percentileFieldDescribe.setFieldNum(null);
        percentileFieldDescribe.setUnique(false);
        percentileFieldDescribe.setStatus("released");
        percentileFieldDescribe.setRequired(false);
    }

    public static PercentileFieldDescribeBuilder builder() {
        return new PercentileFieldDescribeBuilder();
    }

    public PercentileFieldDescribe build() {
        return percentileFieldDescribe;
    }

    public PercentileFieldDescribeBuilder apiName(String apiName) {
        percentileFieldDescribe.setApiName(apiName);
        return this;
    }

    public PercentileFieldDescribeBuilder label(String label) {
        percentileFieldDescribe.setLabel(label);
        return this;
    }

    public PercentileFieldDescribeBuilder required(boolean required) {
        percentileFieldDescribe.setRequired(required);
        return this;
    }

    public PercentileFieldDescribeBuilder helpText(String helpText) {
        percentileFieldDescribe.setHelpText(helpText);
        return this;
    }

}

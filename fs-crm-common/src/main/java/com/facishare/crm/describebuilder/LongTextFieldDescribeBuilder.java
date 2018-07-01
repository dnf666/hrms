package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.paas.metadata.impl.describe.LongTextFieldDescribe;

public class LongTextFieldDescribeBuilder {
    private LongTextFieldDescribe longTextFieldDescribe;

    private LongTextFieldDescribeBuilder() {
        longTextFieldDescribe = new LongTextFieldDescribe();
        longTextFieldDescribe.setCreateTime(System.currentTimeMillis());
        longTextFieldDescribe.setDefineType("package");
        longTextFieldDescribe.setStatus("released");
        longTextFieldDescribe.setFieldNum(null);
        longTextFieldDescribe.setRequired(false);
        longTextFieldDescribe.setUnique(false);
        longTextFieldDescribe.setIsExtend(false);
        longTextFieldDescribe.setActive(true);
    }

    public static LongTextFieldDescribeBuilder builder() {
        return new LongTextFieldDescribeBuilder();
    }

    public LongTextFieldDescribe build() {
        return longTextFieldDescribe;
    }

    public LongTextFieldDescribeBuilder apiName(String apiName) {
        longTextFieldDescribe.setApiName(apiName);
        return this;
    }

    public LongTextFieldDescribeBuilder pattern(String pattern) {
        longTextFieldDescribe.setPattern(pattern);
        return this;
    }

    public LongTextFieldDescribeBuilder maxLength(int maxLength) {
        longTextFieldDescribe.setMaxLength(maxLength);
        return this;
    }

    public LongTextFieldDescribeBuilder label(String label) {
        longTextFieldDescribe.setLabel(label);
        return this;
    }
}

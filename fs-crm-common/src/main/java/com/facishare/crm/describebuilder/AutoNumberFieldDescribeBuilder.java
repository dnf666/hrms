package com.facishare.crm.describebuilder;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.paas.metadata.impl.describe.AutoNumberFieldDescribe;

public class AutoNumberFieldDescribeBuilder {
    private AutoNumberFieldDescribe autoNumberFieldDescribe;

    private AutoNumberFieldDescribeBuilder() {
        autoNumberFieldDescribe = new AutoNumberFieldDescribe();
        autoNumberFieldDescribe.setIsExtend(false);
        autoNumberFieldDescribe.setFieldNum(null);
        autoNumberFieldDescribe.setActive(true);
        autoNumberFieldDescribe.setIndex(true);
        autoNumberFieldDescribe.setStatus("released");
        autoNumberFieldDescribe.setDefineType("package");
        autoNumberFieldDescribe.setPrefix("");
        autoNumberFieldDescribe.setPostfix("");
    }

    public static AutoNumberFieldDescribeBuilder builder() {
        return new AutoNumberFieldDescribeBuilder();
    }

    public AutoNumberFieldDescribe build() {
        return autoNumberFieldDescribe;
    }

    public AutoNumberFieldDescribeBuilder apiName(String apiName) {
        autoNumberFieldDescribe.setApiName(apiName);
        if (CommonConstants.NAME.equals(apiName)) {
            autoNumberFieldDescribe.setDefineType("system");
        }
        return this;
    }

    public AutoNumberFieldDescribeBuilder label(String label) {
        autoNumberFieldDescribe.setLabel(label);
        return this;
    }

    public AutoNumberFieldDescribeBuilder required(boolean required) {
        autoNumberFieldDescribe.setRequired(required);
        return this;
    }

    public AutoNumberFieldDescribeBuilder unique(boolean unique) {
        autoNumberFieldDescribe.setUnique(unique);
        return this;
    }

    public AutoNumberFieldDescribeBuilder status(String status) {
        autoNumberFieldDescribe.setStatus(status);
        return this;
    }

    public AutoNumberFieldDescribeBuilder index(boolean index) {
        autoNumberFieldDescribe.setIndex(index);
        return this;
    }

    public AutoNumberFieldDescribeBuilder defineType(String defineType) {
        autoNumberFieldDescribe.setDefineType(defineType);
        return this;
    }

    public AutoNumberFieldDescribeBuilder prefix(String prefix) {
        autoNumberFieldDescribe.setPrefix(prefix);
        return this;
    }

    public AutoNumberFieldDescribeBuilder postfix(String postfix) {
        autoNumberFieldDescribe.setPostfix(postfix);
        return this;
    }

    public AutoNumberFieldDescribeBuilder serialNumber(int serialNumber) {
        autoNumberFieldDescribe.setSerialNumber(serialNumber);
        return this;
    }

    public AutoNumberFieldDescribeBuilder startNumber(int startNumber) {
        autoNumberFieldDescribe.setSerialNumber(startNumber);
        return this;
    }

    public AutoNumberFieldDescribeBuilder defaultValue(Object defaultValue) {
        autoNumberFieldDescribe.setDefaultValue(defaultValue);
        return this;
    }
}

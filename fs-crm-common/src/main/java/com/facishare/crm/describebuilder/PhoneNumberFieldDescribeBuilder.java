package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.PhoneNumberFieldDescribe;

public class PhoneNumberFieldDescribeBuilder {
    private PhoneNumberFieldDescribe phoneNumberFieldDescribe;

    private PhoneNumberFieldDescribeBuilder() {
        phoneNumberFieldDescribe = new PhoneNumberFieldDescribe();
        phoneNumberFieldDescribe.setCreateTime(System.currentTimeMillis());
        phoneNumberFieldDescribe.setDefineType("package");
        phoneNumberFieldDescribe.setStatus("released");
        phoneNumberFieldDescribe.setFieldNum(null);
        phoneNumberFieldDescribe.setRequired(false);
        phoneNumberFieldDescribe.setUnique(false);
        phoneNumberFieldDescribe.setIsExtend(false);
        phoneNumberFieldDescribe.setActive(true);
        phoneNumberFieldDescribe.setHelpText(null);
    }

    public static PhoneNumberFieldDescribeBuilder builder() {
        return new PhoneNumberFieldDescribeBuilder();
    }

    public PhoneNumberFieldDescribe build() {
        return phoneNumberFieldDescribe;
    }

    public PhoneNumberFieldDescribeBuilder apiName(String apiName) {
        phoneNumberFieldDescribe.setApiName(apiName);
        return this;
    }

    public PhoneNumberFieldDescribeBuilder label(String label) {
        phoneNumberFieldDescribe.setLabel(label);
        return this;
    }

    public PhoneNumberFieldDescribeBuilder required(Boolean isRequired) {
        phoneNumberFieldDescribe.setRequired(isRequired);
        return this;
    }

    public PhoneNumberFieldDescribeBuilder unique(Boolean isUnique) {
        phoneNumberFieldDescribe.setUnique(isUnique);
        return this;
    }

    public PhoneNumberFieldDescribeBuilder helpText(String helpText) {
        phoneNumberFieldDescribe.setHelpText(helpText);
        return this;
    }
}
package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.paas.metadata.impl.describe.TextFieldDescribe;

public class TextFieldDescribeBuilder {
    private TextFieldDescribe textFieldDescribe;

    private TextFieldDescribeBuilder() {
        textFieldDescribe = new TextFieldDescribe();
        textFieldDescribe.setActive(true);
        textFieldDescribe.setStatus("released");
        textFieldDescribe.setDefineType("package");
        textFieldDescribe.setIsExtend(false);
        textFieldDescribe.setUnique(false);
        textFieldDescribe.setRequired(false);
        textFieldDescribe.setDefaultValue(null);
        textFieldDescribe.setDefaultIsExpression(false);
        textFieldDescribe.setDefaultToZero(false);
        textFieldDescribe.setIndex(true);
        textFieldDescribe.setFieldNum(null);
        textFieldDescribe.setCreateTime(System.currentTimeMillis());
        textFieldDescribe.setHelpText(null);
    }

    public static TextFieldDescribeBuilder builder() {
        return new TextFieldDescribeBuilder();
    }

    public TextFieldDescribe build() {
        return textFieldDescribe;
    }

    public TextFieldDescribeBuilder apiName(String apiName) {
        textFieldDescribe.setApiName(apiName);
        if (CommonConstants.NAME.equals(apiName)) {
            textFieldDescribe.setDefineType("system");
            textFieldDescribe.setUnique(true);
            textFieldDescribe.setRequired(true);
        }
        return this;
    }

    public TextFieldDescribeBuilder label(String label) {
        textFieldDescribe.setLabel(label);
        return this;
    }

    public TextFieldDescribeBuilder maxLength(int maxLength) {
        textFieldDescribe.setMaxLength(maxLength);
        return this;
    }

    public TextFieldDescribeBuilder required(Boolean isRequired) {
        textFieldDescribe.setRequired(isRequired);
        return this;
    }

    public TextFieldDescribeBuilder unique(Boolean isUnique) {
        textFieldDescribe.setUnique(isUnique);
        return this;
    }

    public TextFieldDescribeBuilder defaultValue(Object defaultValue) {
        textFieldDescribe.setDefaultValue(defaultValue);
        return this;
    }

    public TextFieldDescribeBuilder defaultIsExpression(boolean defaultIsExpression) {
        textFieldDescribe.setDefaultIsExpression(defaultIsExpression);
        return this;
    }

    public TextFieldDescribeBuilder helpText(String helpText) {
        textFieldDescribe.setHelpText(helpText);
        return this;
    }
}

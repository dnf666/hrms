package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.NumberFieldDescribe;

public class NumberFieldDescribeBuilder {
    private NumberFieldDescribe numberFieldDescribe;

    private NumberFieldDescribeBuilder() {
        numberFieldDescribe = new NumberFieldDescribe();
        numberFieldDescribe.setRequired(false);
        numberFieldDescribe.setUnique(false);
        numberFieldDescribe.setIndex(true);
        numberFieldDescribe.setActive(true);
        numberFieldDescribe.setDefineType("package");
        numberFieldDescribe.setDefaultToZero(true);
        numberFieldDescribe.setIsExtend(false);
        numberFieldDescribe.setFieldNum(null);
        numberFieldDescribe.setStatus("released");
    }

    public static NumberFieldDescribeBuilder builder() {
        return new NumberFieldDescribeBuilder();
    }

    public NumberFieldDescribe build() {
        return numberFieldDescribe;
    }

    public NumberFieldDescribeBuilder apiName(String apiName) {
        numberFieldDescribe.setApiName(apiName);
        return this;
    }

    public NumberFieldDescribeBuilder label(String label) {
        numberFieldDescribe.setLabel(label);
        return this;
    }

    public NumberFieldDescribeBuilder maxLength(int maxLength) {
        numberFieldDescribe.setMaxLength(maxLength);
        return this;
    }

    public NumberFieldDescribeBuilder length(int length) {
        numberFieldDescribe.setLength(length);
        return this;
    }

    public NumberFieldDescribeBuilder decimalPalces(int decimalPlaces) {
        numberFieldDescribe.setDecimalPlaces(decimalPlaces);
        return this;
    }

    public NumberFieldDescribeBuilder roundMode(int roundMode) {
        numberFieldDescribe.setRoundMode(roundMode);
        return this;
    }

    public NumberFieldDescribeBuilder required(boolean required) {
        numberFieldDescribe.setRequired(required);
        return this;
    }

    //用于计算公式
    public NumberFieldDescribeBuilder defaultValue(String value) {
        numberFieldDescribe.setDefaultValue(value);
        return this;
    }

    //用于计算公式
    public NumberFieldDescribeBuilder defaultIsExpression(Boolean isExpression) {
        numberFieldDescribe.setDefaultIsExpression(isExpression);
        return this;
    }
}

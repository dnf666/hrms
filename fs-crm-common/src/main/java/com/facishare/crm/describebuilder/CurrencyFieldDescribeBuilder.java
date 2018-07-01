package com.facishare.crm.describebuilder;

import java.util.Map;

import com.facishare.paas.metadata.impl.describe.CurrencyFieldDescribe;

public class CurrencyFieldDescribeBuilder {
    private CurrencyFieldDescribe currencyFieldDescribe;

    private CurrencyFieldDescribeBuilder() {
        currencyFieldDescribe = new CurrencyFieldDescribe();
        currencyFieldDescribe.setStatus("released");
        currencyFieldDescribe.setUnique(false);
        currencyFieldDescribe.setDefaultToZero(true);
        currencyFieldDescribe.setIndex(true);
        currencyFieldDescribe.setDefineType("package");
        currencyFieldDescribe.setActive(true);
        currencyFieldDescribe.setIsExtend(false);
        currencyFieldDescribe.setFieldNum(null);
    }

    public static CurrencyFieldDescribeBuilder builder() {
        return new CurrencyFieldDescribeBuilder();
    }

    public CurrencyFieldDescribe build() {
        return currencyFieldDescribe;
    }

    public CurrencyFieldDescribeBuilder apiName(String apiName) {
        currencyFieldDescribe.setApiName(apiName);
        return this;
    }

    public CurrencyFieldDescribeBuilder label(String label) {
        currencyFieldDescribe.setLabel(label);
        return this;
    }

    public CurrencyFieldDescribeBuilder maxLength(int maxLength) {
        currencyFieldDescribe.setMaxLength(maxLength);
        return this;
    }

    public CurrencyFieldDescribeBuilder length(int length) {
        currencyFieldDescribe.setLength(length);
        return this;
    }

    public CurrencyFieldDescribeBuilder decimalPlaces(int decimalPlaces) {
        currencyFieldDescribe.setDecimalPlaces(decimalPlaces);
        return this;
    }

    public CurrencyFieldDescribeBuilder currencyUnit(String currencyUnit) {
        currencyFieldDescribe.setCurrencyUnit(currencyUnit);
        return this;
    }

    public CurrencyFieldDescribeBuilder required(boolean required) {
        currencyFieldDescribe.setRequired(required);
        return this;
    }

    public CurrencyFieldDescribeBuilder roundMode(int roundMode) {
        currencyFieldDescribe.setRoundMode(roundMode);
        return this;
    }

    public CurrencyFieldDescribeBuilder config(Map<String, Object> config) {
        currencyFieldDescribe.setConfig(config);
        return this;
    }

    public CurrencyFieldDescribeBuilder helpText(String helpText) {
        currencyFieldDescribe.setHelpText(helpText);
        return this;
    }
}

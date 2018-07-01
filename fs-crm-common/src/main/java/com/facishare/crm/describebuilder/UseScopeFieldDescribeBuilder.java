package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.paas.metadata.impl.describe.UseScopeFieldDescribe;

public class UseScopeFieldDescribeBuilder {
    private UseScopeFieldDescribe useScopeFieldDescribe;

    private UseScopeFieldDescribeBuilder() {
        useScopeFieldDescribe = new UseScopeFieldDescribe();
        useScopeFieldDescribe.setActive(true);
        useScopeFieldDescribe.setDefineType("package");
        useScopeFieldDescribe.setCreateTime(System.currentTimeMillis());
        useScopeFieldDescribe.setFieldNum(null);
        useScopeFieldDescribe.setStatus("released");
        useScopeFieldDescribe.setIsExtend(false);
        useScopeFieldDescribe.setUnique(false);
    }

    public static UseScopeFieldDescribeBuilder builder() {
        return new UseScopeFieldDescribeBuilder();
    }

    public UseScopeFieldDescribe build() {
        return useScopeFieldDescribe;
    }

    public UseScopeFieldDescribeBuilder apiName(String apiName) {
        useScopeFieldDescribe.setApiName(apiName);
        return this;
    }

    public UseScopeFieldDescribeBuilder label(String label) {
        useScopeFieldDescribe.setLabel(label);
        return this;
    }

    public UseScopeFieldDescribeBuilder targetApiName(String targetApiName) {
        useScopeFieldDescribe.setTargetApiName(targetApiName);
        return this;
    }

    public UseScopeFieldDescribeBuilder index(boolean index) {
        useScopeFieldDescribe.setIndex(index);
        return this;
    }

    public UseScopeFieldDescribeBuilder required(boolean required) {
        useScopeFieldDescribe.setRequired(required);
        return this;
    }

    public UseScopeFieldDescribeBuilder unique(boolean unique) {
        useScopeFieldDescribe.setUnique(unique);
        return this;
    }

    public UseScopeFieldDescribeBuilder maxLength(Integer maxLength) {
        useScopeFieldDescribe.setMaxLength(maxLength);
        return this;
    }

    public UseScopeFieldDescribeBuilder defaultIsExpression(boolean defaultIsExpression) {
        useScopeFieldDescribe.setDefaultIsExpression(defaultIsExpression);
        return this;
    }

    public UseScopeFieldDescribeBuilder defaultValue(Object defaultValue) {
        useScopeFieldDescribe.setDefaultValue(defaultValue);
        return this;
    }

    public UseScopeFieldDescribeBuilder expressionType(String expressionType) {
        useScopeFieldDescribe.set("expression_type", expressionType);
        return this;
    }

}

package com.facishare.crm.describebuilder;

import java.util.List;
import java.util.Map;

import com.facishare.paas.metadata.impl.describe.BooleanFieldDescribe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BooleanFieldDescribeBuilder {
    private BooleanFieldDescribe booleanFieldDescribe;

    private BooleanFieldDescribeBuilder() {
        booleanFieldDescribe = new BooleanFieldDescribe();
        booleanFieldDescribe.setActive(true);
        booleanFieldDescribe.setDefineType("package");
        booleanFieldDescribe.setStatus("released");
        booleanFieldDescribe.setCreateTime(System.currentTimeMillis());
        booleanFieldDescribe.setIsExtend(false);
        booleanFieldDescribe.setFieldNum(null);
        booleanFieldDescribe.setRequired(true);
        booleanFieldDescribe.setUnique(false);
        booleanFieldDescribe.setIndex(true);
        Map<String, Object> optionTrue = Maps.newHashMap();
        optionTrue.put("label", "是");
        optionTrue.put("value", true);
        Map<String, Object> optionFalse = Maps.newHashMap();
        optionFalse.put("label", "否");
        optionFalse.put("value", false);
        List<Map> options = Lists.newArrayList(optionTrue, optionFalse);
        booleanFieldDescribe.set("options", options);
    }

    public static BooleanFieldDescribeBuilder builder() {
        return new BooleanFieldDescribeBuilder();
    }

    public BooleanFieldDescribe build() {
        return booleanFieldDescribe;
    }

    public BooleanFieldDescribeBuilder apiName(String apiName) {
        booleanFieldDescribe.setApiName(apiName);
        return this;
    }

    public BooleanFieldDescribeBuilder label(String label) {
        booleanFieldDescribe.setLabel(label);
        return this;
    }

    public BooleanFieldDescribeBuilder required(boolean required) {
        booleanFieldDescribe.setRequired(required);
        return this;
    }

    public BooleanFieldDescribeBuilder unique(boolean unique) {
        booleanFieldDescribe.setUnique(unique);
        return this;
    }

    public BooleanFieldDescribeBuilder index(boolean index) {
        booleanFieldDescribe.setIndex(index);
        return this;
    }

    public BooleanFieldDescribeBuilder defaultValue(boolean defaultValue) {
        booleanFieldDescribe.setDefaultValue(defaultValue);
        return this;
    }

    public BooleanFieldDescribeBuilder setOptions(List<Map> options) {
        booleanFieldDescribe.set("options", options);
        return this;
    }
}

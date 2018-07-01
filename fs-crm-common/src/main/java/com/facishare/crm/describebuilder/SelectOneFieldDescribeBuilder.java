package com.facishare.crm.describebuilder;

import java.util.List;
import java.util.Map;

import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;

public class SelectOneFieldDescribeBuilder {
    private SelectOneFieldDescribe selectOneFieldDescribe;

    private SelectOneFieldDescribeBuilder() {
        selectOneFieldDescribe = new SelectOneFieldDescribe();
        selectOneFieldDescribe.setActive(true);
        selectOneFieldDescribe.setIsExtend(false);
        selectOneFieldDescribe.setIndex(true);
        selectOneFieldDescribe.setStatus("released");
        selectOneFieldDescribe.setDefineType("package");
        selectOneFieldDescribe.setUnique(false);
        selectOneFieldDescribe.setFieldNum(null);
    }

    public static SelectOneFieldDescribeBuilder builder() {
        return new SelectOneFieldDescribeBuilder();
    }

    public SelectOneFieldDescribe build() {
        return selectOneFieldDescribe;
    }

    public SelectOneFieldDescribeBuilder apiName(String apiName) {
        selectOneFieldDescribe.setApiName(apiName);
        return this;
    }

    public SelectOneFieldDescribeBuilder label(String label) {
        selectOneFieldDescribe.setLabel(label);
        return this;
    }

    public SelectOneFieldDescribeBuilder required(boolean required) {
        selectOneFieldDescribe.setRequired(required);
        return this;
    }

    public SelectOneFieldDescribeBuilder defaultValud(Object defaultValue) {
        selectOneFieldDescribe.setDefaultValue(defaultValue);
        return this;
    }

    public SelectOneFieldDescribeBuilder selectOptions(List<ISelectOption> selectOptions) {
        selectOneFieldDescribe.setSelectOptions(selectOptions);
        return this;
    }

    public SelectOneFieldDescribeBuilder helpText(String helpText) {
        selectOneFieldDescribe.setHelpText(helpText);
        return this;
    }

    public SelectOneFieldDescribeBuilder config(Map<String, Object> config) {
        selectOneFieldDescribe.setConfig(config);
        return this;
    }
}

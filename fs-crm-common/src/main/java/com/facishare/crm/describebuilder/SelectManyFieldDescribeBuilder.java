package com.facishare.crm.describebuilder;

import java.util.List;
import java.util.Map;

import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.impl.describe.SelectManyFieldDescribe;

public class SelectManyFieldDescribeBuilder {
    private SelectManyFieldDescribe selectManyFieldDescribe;

    private SelectManyFieldDescribeBuilder() {
        selectManyFieldDescribe = new SelectManyFieldDescribe();
        selectManyFieldDescribe.setRequired(true);
        selectManyFieldDescribe.setStatus("released");
        selectManyFieldDescribe.setDefineType("package");
        selectManyFieldDescribe.setIndex(true);
        selectManyFieldDescribe.setActive(true);
        selectManyFieldDescribe.setFieldNum(null);
        selectManyFieldDescribe.setIsExtend(false);
    }

    public static SelectManyFieldDescribeBuilder builder() {
        return new SelectManyFieldDescribeBuilder();
    }

    public SelectManyFieldDescribe build() {
        return selectManyFieldDescribe;
    }

    public SelectManyFieldDescribeBuilder apiName(String apiName) {
        selectManyFieldDescribe.setApiName(apiName);
        return this;
    }

    public SelectManyFieldDescribeBuilder label(String label) {
        selectManyFieldDescribe.setLabel(label);
        return this;
    }

    public SelectManyFieldDescribeBuilder helpText(String helpText) {
        selectManyFieldDescribe.setHelpText(helpText);
        return this;
    }

    public SelectManyFieldDescribeBuilder selectOptions(List<ISelectOption> selectOptions) {
        selectManyFieldDescribe.setSelectOptions(selectOptions);
        return this;
    }

    public SelectManyFieldDescribeBuilder config(Map<String, Object> config) {
        selectManyFieldDescribe.setConfig(config);
        return this;
    }
}

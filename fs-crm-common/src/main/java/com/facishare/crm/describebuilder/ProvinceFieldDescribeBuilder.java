package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.ProvinceFieldDescribe;

/**
 * Created by linchf on 2018/1/10.
 */
public class ProvinceFieldDescribeBuilder {
    private ProvinceFieldDescribe provinceFieldDescribe;

    private ProvinceFieldDescribeBuilder() {
        provinceFieldDescribe = new ProvinceFieldDescribe();
        provinceFieldDescribe.setActive(true);
        provinceFieldDescribe.setIsExtend(false);
        provinceFieldDescribe.setIndex(true);
        provinceFieldDescribe.setStatus("release");
        provinceFieldDescribe.setDefineType("package");
        provinceFieldDescribe.setUnique(false);
        provinceFieldDescribe.setUsedIn("component");
    }

    public static ProvinceFieldDescribeBuilder builder() {
        return new ProvinceFieldDescribeBuilder();
    }

    public ProvinceFieldDescribe build() {
        return provinceFieldDescribe;
    }

    public ProvinceFieldDescribeBuilder apiName(String apiName) {
        provinceFieldDescribe.setApiName(apiName);
        return this;
    }

    public ProvinceFieldDescribeBuilder label(String label) {
        provinceFieldDescribe.setLabel(label);
        return this;
    }

    public ProvinceFieldDescribeBuilder cascadeParentApiName(String parentApiName) {
        provinceFieldDescribe.setCascadeParentApiName(parentApiName);
        return this;
    }
}

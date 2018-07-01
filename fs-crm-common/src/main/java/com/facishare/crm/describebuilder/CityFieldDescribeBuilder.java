package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.CityFiledDescribe;

/**
 * Created by linchf on 2018/1/10.
 */
public class CityFieldDescribeBuilder {
    private CityFiledDescribe cityFiledDescribe;

    private CityFieldDescribeBuilder() {
        cityFiledDescribe = new CityFiledDescribe();
        cityFiledDescribe.setActive(true);
        cityFiledDescribe.setIsExtend(false);
        cityFiledDescribe.setIndex(true);
        cityFiledDescribe.setStatus("release");
        cityFiledDescribe.setDefineType("package");
        cityFiledDescribe.setUnique(false);
        cityFiledDescribe.setUsedIn("component");
    }

    public static CityFieldDescribeBuilder builder() {
        return new CityFieldDescribeBuilder();
    }

    public CityFiledDescribe build() {
        return cityFiledDescribe;
    }

    public CityFieldDescribeBuilder apiName(String apiName) {
        cityFiledDescribe.setApiName(apiName);
        return this;
    }

    public CityFieldDescribeBuilder label(String label) {
        cityFiledDescribe.setLabel(label);
        return this;
    }

    public CityFieldDescribeBuilder cascadeParentApiName(String parentApiName) {
        cityFiledDescribe.setCascadeParentApiName(parentApiName);
        return this;
    }
}

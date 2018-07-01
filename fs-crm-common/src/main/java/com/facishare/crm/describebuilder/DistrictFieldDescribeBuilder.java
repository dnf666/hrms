package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.DistrictFieldDescribe;

/**
 * Created by linchf on 2018/1/10.
 */
public class DistrictFieldDescribeBuilder {
    private DistrictFieldDescribe districtFieldDescribe;

    private DistrictFieldDescribeBuilder() {
        districtFieldDescribe = new DistrictFieldDescribe();
        districtFieldDescribe.setActive(true);
        districtFieldDescribe.setIsExtend(false);
        districtFieldDescribe.setIndex(true);
        districtFieldDescribe.setStatus("release");
        districtFieldDescribe.setDefineType("package");
        districtFieldDescribe.setUnique(false);
        districtFieldDescribe.setUsedIn("component");
    }

    public static DistrictFieldDescribeBuilder builder() {
        return new DistrictFieldDescribeBuilder();
    }

    public DistrictFieldDescribe build() {
        return districtFieldDescribe;
    }

    public DistrictFieldDescribeBuilder apiName(String apiName) {
        districtFieldDescribe.setApiName(apiName);
        return this;
    }

    public DistrictFieldDescribeBuilder label(String label) {
        districtFieldDescribe.setLabel(label);
        return this;
    }

    public DistrictFieldDescribeBuilder cascadeParentApiName(String parentApiName) {
        districtFieldDescribe.setCascadeParentApiName(parentApiName);
        return this;
    }
}

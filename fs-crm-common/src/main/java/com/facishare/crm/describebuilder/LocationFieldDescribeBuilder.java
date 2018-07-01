package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.LocationFieldDescribe;

/**
 * Created by linchf on 2018/1/12.
 */
public class LocationFieldDescribeBuilder {

    private LocationFieldDescribe locationFieldDescribe;

    public LocationFieldDescribeBuilder() {
        locationFieldDescribe = new LocationFieldDescribe();
        locationFieldDescribe.setActive(true);
        locationFieldDescribe.setIsExtend(false);
        locationFieldDescribe.setIndex(true);
        locationFieldDescribe.setStatus("release");
        locationFieldDescribe.setDefineType("package");
        locationFieldDescribe.setUnique(false);
        locationFieldDescribe.setUsedIn("component");
    }

    public static LocationFieldDescribeBuilder builder() {
        return new LocationFieldDescribeBuilder();
    }

    public LocationFieldDescribe build() {
        return locationFieldDescribe;
    }

    public LocationFieldDescribeBuilder apiName(String apiName) {
        locationFieldDescribe.setApiName(apiName);
        return this;
    }

    public LocationFieldDescribeBuilder label(String label) {
        locationFieldDescribe.setLabel(label);
        return this;
    }

    public LocationFieldDescribeBuilder active(Boolean active) {
        locationFieldDescribe.setActive(active);
        return this;
    }

}

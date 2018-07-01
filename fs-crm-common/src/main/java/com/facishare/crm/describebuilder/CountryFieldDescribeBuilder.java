package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.CountryFieldDescribe;

/**
 * Created by linchf on 2018/1/10.
 */
public class CountryFieldDescribeBuilder {
    private CountryFieldDescribe countryFieldDescribe;

    private CountryFieldDescribeBuilder() {
        countryFieldDescribe = new CountryFieldDescribe();
        countryFieldDescribe.setActive(true);
        countryFieldDescribe.setIsExtend(false);
        countryFieldDescribe.setIndex(true);
        countryFieldDescribe.setStatus("release");
        countryFieldDescribe.setDefineType("package");
        countryFieldDescribe.setUnique(false);
        countryFieldDescribe.setUsedIn("component");
    }

    public static CountryFieldDescribeBuilder builder() {
        return new CountryFieldDescribeBuilder();
    }

    public CountryFieldDescribe build() {
        return countryFieldDescribe;
    }

    public CountryFieldDescribeBuilder apiName(String apiName) {
        countryFieldDescribe.setApiName(apiName);
        return this;
    }

    public CountryFieldDescribeBuilder label(String label) {
        countryFieldDescribe.setLabel(label);
        return this;
    }






}

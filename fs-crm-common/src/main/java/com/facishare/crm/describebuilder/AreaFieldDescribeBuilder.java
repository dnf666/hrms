package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.AreaFieldDescribe;

/**
 * Created by linchf on 2018/1/11.
 */
public class AreaFieldDescribeBuilder {
    private AreaFieldDescribe areaFieldDescribe;

    private AreaFieldDescribeBuilder() {
        areaFieldDescribe = new AreaFieldDescribe();
        areaFieldDescribe.setActive(true);
        areaFieldDescribe.setIsExtend(false);
        areaFieldDescribe.setIndex(true);
        areaFieldDescribe.setStatus("release");
        areaFieldDescribe.setDefineType("package");
        areaFieldDescribe.setUnique(false);
    }
    public static AreaFieldDescribeBuilder builder() {
        return new AreaFieldDescribeBuilder();
    }

    public AreaFieldDescribe build() {
        return areaFieldDescribe;
    }

    public AreaFieldDescribeBuilder apiName(String apiName) {
        areaFieldDescribe.setApiName(apiName);
        return this;
    }

    public AreaFieldDescribeBuilder label(String label) {
        areaFieldDescribe.setLabel(label);
        return this;
    }

    public AreaFieldDescribeBuilder areaCity(String city) {
        areaFieldDescribe.setAreaCityFieldApiName(city);
        return this;
    }

    public AreaFieldDescribeBuilder areaCountry(String country) {
        areaFieldDescribe.setAreaCountryFieldApiName(country);
        return this;
    }

    public AreaFieldDescribeBuilder areaDetailAddress(String detailAddress) {
        areaFieldDescribe.setAreaDetailAddressFieldApiName(detailAddress);
        return this;
    }

    public AreaFieldDescribeBuilder areaDistrict(String district) {
        areaFieldDescribe.setAreaDistrictFieldApiName(district);
        return this;
    }

    public AreaFieldDescribeBuilder areaLocation(String location) {
        areaFieldDescribe.setAreaLocationFieldApiName(location);
        return this;
    }

    public AreaFieldDescribeBuilder areaProvince(String province) {
        areaFieldDescribe.setAreaProvinceFieldApiName(province);
        return this;
    }



}

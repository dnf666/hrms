package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.paas.metadata.impl.describe.DateFieldDescribe;

public class DateFieldDescribeBuilder {
    private DateFieldDescribe dateFieldDescribe;

    private DateFieldDescribeBuilder() {
        dateFieldDescribe = new DateFieldDescribe();
        dateFieldDescribe.setDateFormat("yyyy-MM-dd");
        dateFieldDescribe.setActive(true);
        dateFieldDescribe.setIsExtend(false);
        dateFieldDescribe.setStatus("released");
        dateFieldDescribe.setDefineType("package");
        dateFieldDescribe.setFieldNum(null);
        dateFieldDescribe.setUnique(false);
        dateFieldDescribe.setCreateTime(System.currentTimeMillis());
    }

    public static DateFieldDescribeBuilder builder() {
        return new DateFieldDescribeBuilder();
    }

    public DateFieldDescribe build() {
        return dateFieldDescribe;
    }

    public DateFieldDescribeBuilder apiName(String apiName) {
        dateFieldDescribe.setApiName(apiName);
        return this;
    }

    public DateFieldDescribeBuilder label(String label) {
        dateFieldDescribe.setLabel(label);
        return this;
    }

    public DateFieldDescribeBuilder unique(boolean unique) {
        dateFieldDescribe.setUnique(unique);
        return this;
    }

    public DateFieldDescribeBuilder index(boolean index) {
        dateFieldDescribe.setIndex(index);
        return this;
    }

    public DateFieldDescribeBuilder required(boolean required) {
        dateFieldDescribe.setRequired(required);
        return this;
    }

    public DateFieldDescribeBuilder format(String format) {
        dateFieldDescribe.setDateFormat(format);
        return this;
    }
}

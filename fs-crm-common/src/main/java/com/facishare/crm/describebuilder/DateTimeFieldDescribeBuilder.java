package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.DateTimeFieldDescribe;

public class DateTimeFieldDescribeBuilder {
    private DateTimeFieldDescribe dateTimeFieldDescribe;

    private DateTimeFieldDescribeBuilder() {
        dateTimeFieldDescribe = new DateTimeFieldDescribe();
        dateTimeFieldDescribe.setActive(true);
        dateTimeFieldDescribe.setIsExtend(false);
        dateTimeFieldDescribe.setFieldNum(null);
        dateTimeFieldDescribe.setDefineType("package");
        dateTimeFieldDescribe.setStatus("released");
    }

    public static DateTimeFieldDescribeBuilder builder() {
        return new DateTimeFieldDescribeBuilder();
    }

    public DateTimeFieldDescribe build() {
        return dateTimeFieldDescribe;
    }

    public DateTimeFieldDescribeBuilder apiName(String apiName) {
        dateTimeFieldDescribe.setApiName(apiName);
        return this;
    }

    public DateTimeFieldDescribeBuilder label(String label) {
        dateTimeFieldDescribe.setLabel(label);
        return this;
    }

    public DateTimeFieldDescribeBuilder required(boolean required) {
        dateTimeFieldDescribe.setRequired(required);
        return this;
    }

    public DateTimeFieldDescribeBuilder unique(boolean unique) {
        dateTimeFieldDescribe.setUnique(unique);
        return this;
    }

    public DateTimeFieldDescribeBuilder status(String status) {
        dateTimeFieldDescribe.setStatus(status);
        return this;
    }

    public DateTimeFieldDescribeBuilder defineType(String defineType) {
        dateTimeFieldDescribe.setDefineType(defineType);
        return this;
    }

    public DateTimeFieldDescribeBuilder dateFormat(String dateFormat) {
        dateTimeFieldDescribe.setDateFormat(dateFormat);
        return this;
    }
}

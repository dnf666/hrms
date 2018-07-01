package com.facishare.crm.describebuilder;

import java.util.Map;

import com.facishare.paas.metadata.impl.describe.ImageFieldDescribe;

public class ImageFieldDescribeBuilder {
    private ImageFieldDescribe imageFieldDescribe;

    private ImageFieldDescribeBuilder() {
        this.imageFieldDescribe = new ImageFieldDescribe();
        imageFieldDescribe.setFileSizeLimit(10485760L);
        imageFieldDescribe.setFileAmountLimit(10);
        imageFieldDescribe.setActive(true);
        imageFieldDescribe.setCreateTime(System.currentTimeMillis());
        imageFieldDescribe.setFieldNum(null);
        imageFieldDescribe.setDefineType("package");
        imageFieldDescribe.setStatus("released");
        imageFieldDescribe.setRequired(false);
        imageFieldDescribe.setIsExtend(false);
    }

    public static ImageFieldDescribeBuilder builder() {
        return new ImageFieldDescribeBuilder();
    }

    public ImageFieldDescribe build() {
        return imageFieldDescribe;
    }

    public ImageFieldDescribeBuilder apiName(String apiName) {
        imageFieldDescribe.setApiName(apiName);
        return this;
    }

    public ImageFieldDescribeBuilder label(String label) {
        imageFieldDescribe.setLabel(label);
        return this;
    }

    public ImageFieldDescribeBuilder required(boolean required) {
        imageFieldDescribe.setRequired(required);
        return this;
    }

    public ImageFieldDescribeBuilder fileAmountLimit(int fileAmountLimit) {
        imageFieldDescribe.setFileAmountLimit(fileAmountLimit);
        return this;
    }

    public ImageFieldDescribeBuilder config(Map<String, Object> config) {
        imageFieldDescribe.setConfig(config);
        return this;
    }

    public ImageFieldDescribeBuilder helpText(String helpText) {
        imageFieldDescribe.setHelpText(helpText);
        return this;
    }
}

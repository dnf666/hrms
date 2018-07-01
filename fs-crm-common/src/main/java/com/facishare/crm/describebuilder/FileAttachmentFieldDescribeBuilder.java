package com.facishare.crm.describebuilder;

import java.util.List;

import com.facishare.paas.metadata.impl.describe.FileAttachmentFieldDescribe;

public class FileAttachmentFieldDescribeBuilder {
    private FileAttachmentFieldDescribe fileAttachmentFieldDescribe;

    private FileAttachmentFieldDescribeBuilder() {
        fileAttachmentFieldDescribe = new FileAttachmentFieldDescribe();
        fileAttachmentFieldDescribe.setActive(true);
        fileAttachmentFieldDescribe.setDefineType("package");
        fileAttachmentFieldDescribe.setIsExtend(false);
        fileAttachmentFieldDescribe.setStatus("released");
        fileAttachmentFieldDescribe.setRequired(false);
        fileAttachmentFieldDescribe.setUnique(false);
        fileAttachmentFieldDescribe.setFieldNum(null);
    }

    public static FileAttachmentFieldDescribeBuilder builder() {
        return new FileAttachmentFieldDescribeBuilder();
    }

    public FileAttachmentFieldDescribe build() {
        return fileAttachmentFieldDescribe;
    }

    public FileAttachmentFieldDescribeBuilder apiName(String apiName) {
        fileAttachmentFieldDescribe.setApiName(apiName);
        return this;
    }

    public FileAttachmentFieldDescribeBuilder label(String label) {
        fileAttachmentFieldDescribe.setLabel(label);
        return this;
    }

    public FileAttachmentFieldDescribeBuilder supportFileTypes(List<String> supportFileTypes) {
        fileAttachmentFieldDescribe.setSupportFileTypes(supportFileTypes);
        return this;
    }

    public FileAttachmentFieldDescribeBuilder fileAmountLimit(int amountLimit) {
        fileAttachmentFieldDescribe.setFileAmountLimit(amountLimit);
        return this;
    }

    public FileAttachmentFieldDescribeBuilder fileSizeLimit(long fileSizeLimit) {
        fileAttachmentFieldDescribe.setFileSizeLimit(fileSizeLimit);
        return this;
    }
}

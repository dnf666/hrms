package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.MasterDetailFieldDescribe;

public class MasterDetailFieldDescribeBuilder {
    private MasterDetailFieldDescribe masterDetailFieldDescribe;

    private MasterDetailFieldDescribeBuilder() {
        this.masterDetailFieldDescribe = new MasterDetailFieldDescribe();
        masterDetailFieldDescribe.setActive(true);
        masterDetailFieldDescribe.setIsExtend(false);
        masterDetailFieldDescribe.setFieldNum(null);
        masterDetailFieldDescribe.setStatus("released");
        masterDetailFieldDescribe.setDefineType("package");
    }

    public static MasterDetailFieldDescribeBuilder builder() {
        return new MasterDetailFieldDescribeBuilder();
    }

    public MasterDetailFieldDescribe build() {
        return masterDetailFieldDescribe;
    }

    public MasterDetailFieldDescribeBuilder apiName(String apiName) {
        masterDetailFieldDescribe.setApiName(apiName);
        return this;
    }

    public MasterDetailFieldDescribeBuilder required(boolean required) {
        masterDetailFieldDescribe.setRequired(required);
        return this;
    }

    public MasterDetailFieldDescribeBuilder label(String label) {
        masterDetailFieldDescribe.setLabel(label);
        return this;
    }

    public MasterDetailFieldDescribeBuilder unique(boolean unique) {
        masterDetailFieldDescribe.setUnique(unique);
        return this;
    }

    public MasterDetailFieldDescribeBuilder status(String status) {
        masterDetailFieldDescribe.setStatus(status);
        return this;
    }

    public MasterDetailFieldDescribeBuilder index(boolean index) {
        masterDetailFieldDescribe.setIndex(index);
        return this;
    }

    public MasterDetailFieldDescribeBuilder defineType(String defineType) {
        masterDetailFieldDescribe.setDefineType(defineType);
        return this;
    }

    public MasterDetailFieldDescribeBuilder targetApiName(String targetApiName) {
        masterDetailFieldDescribe.setTargetApiName(targetApiName);
        return this;
    }

    public MasterDetailFieldDescribeBuilder targetRelatedListLabel(String targetRelatedListLabel) {
        masterDetailFieldDescribe.setTargetRelatedListLabel(targetRelatedListLabel);
        return this;
    }

    public MasterDetailFieldDescribeBuilder targetRelatedListName(String targetRelatedListName) {
        masterDetailFieldDescribe.setTargetRelatedListName(targetRelatedListName);
        return this;
    }

    public MasterDetailFieldDescribeBuilder isCreateWhenMasterCreate(boolean isCreateWhenMasterCreate) {
        masterDetailFieldDescribe.setIsCreateWhenMasterCreate(isCreateWhenMasterCreate);
        return this;
    }

    public MasterDetailFieldDescribeBuilder isRequiredWhenMasterCreate(boolean isRequiredWhenMasterCreate) {
        masterDetailFieldDescribe.setIsRequiredWhenMasterCreate(isRequiredWhenMasterCreate);
        return this;
    }

}

package com.facishare.crm.describebuilder;

import java.util.LinkedHashMap;
import java.util.List;

import com.facishare.paas.metadata.api.describe.ReferenceDeleteAction;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;

import java.util.LinkedHashMap;
import java.util.List;

public class ObjectReferenceFieldDescribeBuilder {
    private ObjectReferenceFieldDescribe objectReferenceFieldDescribe;

    private ObjectReferenceFieldDescribeBuilder() {
        objectReferenceFieldDescribe = new ObjectReferenceFieldDescribe();
        objectReferenceFieldDescribe.setStatus("released");
        objectReferenceFieldDescribe.setDefineType("package");
        objectReferenceFieldDescribe.setIsExtend(false);
        objectReferenceFieldDescribe.setUnique(false);
        objectReferenceFieldDescribe.setIndex(true);
        objectReferenceFieldDescribe.setActive(true);
        objectReferenceFieldDescribe.setFieldNum(null);
        objectReferenceFieldDescribe.setActionOnTargetDelete(ReferenceDeleteAction.set_null);
    }

    public static ObjectReferenceFieldDescribeBuilder builder() {
        return new ObjectReferenceFieldDescribeBuilder();
    }

    public ObjectReferenceFieldDescribe build() {
        return objectReferenceFieldDescribe;
    }

    public ObjectReferenceFieldDescribeBuilder apiName(String apiName) {
        objectReferenceFieldDescribe.setApiName(apiName);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder label(String label) {
        objectReferenceFieldDescribe.setLabel(label);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder unique(boolean unique) {
        objectReferenceFieldDescribe.setUnique(unique);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder required(boolean required) {
        objectReferenceFieldDescribe.setRequired(required);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder targetApiName(String targetApiName) {
        objectReferenceFieldDescribe.setTargetApiName(targetApiName);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder targetRelatedListLabel(String targetRelatedListLabel) {
        objectReferenceFieldDescribe.setTargetRelatedListLabel(targetRelatedListLabel);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder targetRelatedListName(String targetRelatedListName) {
        objectReferenceFieldDescribe.setTargetRelatedListName(targetRelatedListName);
        return this;
    }

    public ObjectReferenceFieldDescribeBuilder wheres(List<LinkedHashMap> wheres) {
        objectReferenceFieldDescribe.setWheres(wheres);
        return this;
    }
}

package com.facishare.crm.describebuilder;

import java.util.List;

import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;

public class RecordTypeFieldDescribeBuilder {
    private RecordTypeFieldDescribe recordTypeFieldDescribe;

    private RecordTypeFieldDescribeBuilder() {
        recordTypeFieldDescribe = new RecordTypeFieldDescribe();
        recordTypeFieldDescribe.setUnique(false);
        recordTypeFieldDescribe.setStatus("released");
        recordTypeFieldDescribe.setIndex(true);
        recordTypeFieldDescribe.setIsExtend(false);
        recordTypeFieldDescribe.setFieldNum(null);
        recordTypeFieldDescribe.setActive(true);
        recordTypeFieldDescribe.setRequired(true);
        recordTypeFieldDescribe.setDefineType("package");
    }

    public static RecordTypeFieldDescribeBuilder builder() {
        return new RecordTypeFieldDescribeBuilder();
    }

    public RecordTypeFieldDescribe build() {
        return recordTypeFieldDescribe;
    }

    public RecordTypeFieldDescribeBuilder apiName(String apiName) {
        recordTypeFieldDescribe.setApiName(apiName);
        recordTypeFieldDescribe.setIndexName(apiName);
        return this;
    }

    public RecordTypeFieldDescribeBuilder label(String label) {
        recordTypeFieldDescribe.setLabel(label);
        return this;
    }

    public RecordTypeFieldDescribeBuilder recordTypeOptions(List<IRecordTypeOption> recordTypeOptions) {
        recordTypeFieldDescribe.setRecordTypeOptions(recordTypeOptions);
        return this;
    }
}

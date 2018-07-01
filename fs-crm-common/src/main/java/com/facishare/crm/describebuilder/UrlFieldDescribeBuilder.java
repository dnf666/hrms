package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.paas.metadata.impl.describe.URLFieldDescribe;

public class UrlFieldDescribeBuilder {
    private URLFieldDescribe urlFieldDescribe;

    private UrlFieldDescribeBuilder() {
        urlFieldDescribe = new URLFieldDescribe();
        urlFieldDescribe.setActive(true);
        urlFieldDescribe.setCreateTime(System.currentTimeMillis());
        urlFieldDescribe.setFieldNum(null);
        urlFieldDescribe.setIsExtend(false);
        urlFieldDescribe.setUnique(false);
        urlFieldDescribe.setStatus("released");
        urlFieldDescribe.setDefineType("package");
    }

    public UrlFieldDescribeBuilder apiName(String apiName) {
        urlFieldDescribe.setApiName(apiName);
        return this;
    }

    public UrlFieldDescribeBuilder label(String label) {
        urlFieldDescribe.setLabel(label);
        return this;
    }

    public static UrlFieldDescribeBuilder builder() {
        return new UrlFieldDescribeBuilder();
    }

    public URLFieldDescribe build() {
        return urlFieldDescribe;
    }
}

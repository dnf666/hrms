package com.facishare.crm.describebuilder;

import java.util.Date;

import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;

public class QuoteFieldDescribeBuilder {

    private QuoteFieldDescribe quoteFieldDescribe;

    private QuoteFieldDescribeBuilder() {
        quoteFieldDescribe = new QuoteFieldDescribe();
        quoteFieldDescribe.setStatus("released");
        quoteFieldDescribe.setIndex(true);
        quoteFieldDescribe.setFieldNum(null);
        quoteFieldDescribe.setIsExtend(false);
        quoteFieldDescribe.setDefineType("package");
        quoteFieldDescribe.setActive(true);
        quoteFieldDescribe.setCreateTime(System.currentTimeMillis());
    }

    public static QuoteFieldDescribeBuilder builder() {
        return new QuoteFieldDescribeBuilder();
    }

    public QuoteFieldDescribe build() {
        return quoteFieldDescribe;
    }

    public QuoteFieldDescribeBuilder apiName(String apiName) {
        quoteFieldDescribe.setApiName(apiName);
        return this;
    }

    public QuoteFieldDescribeBuilder label(String label) {
        quoteFieldDescribe.setLabel(label);
        return this;
    }

    public QuoteFieldDescribeBuilder unique(boolean unique) {
        quoteFieldDescribe.setUnique(unique);
        return this;
    }

    public QuoteFieldDescribeBuilder required(boolean required) {
        quoteFieldDescribe.setRequired(required);
        return this;
    }

    public QuoteFieldDescribeBuilder quoteFieldType(String quoteFieldType) {
        quoteFieldDescribe.setQuoteFieldType(quoteFieldType);
        return this;
    }

    public QuoteFieldDescribeBuilder quoteField(String quoteField) {
        quoteFieldDescribe.setQuoteField(quoteField);
        return this;
    }
}

package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.EmbeddedObjectListFieldDescribe;

public class EmbeddedObjectListFieldDescribeBuilder {
    private EmbeddedObjectListFieldDescribe embeddedObjectListFieldDescribe;

    private EmbeddedObjectListFieldDescribeBuilder() {
        embeddedObjectListFieldDescribe = new EmbeddedObjectListFieldDescribe();
        embeddedObjectListFieldDescribe.setActive(true);
        embeddedObjectListFieldDescribe.setDefineType("package");
        embeddedObjectListFieldDescribe.setStatus("released");
        embeddedObjectListFieldDescribe.setIndex(true);
        embeddedObjectListFieldDescribe.setRequired(false);
        embeddedObjectListFieldDescribe.setIsExtend(false);
        embeddedObjectListFieldDescribe.setFieldNum(null);
        embeddedObjectListFieldDescribe.setUnique(false);
    }

    public static EmbeddedObjectListFieldDescribeBuilder builder() {
        return new EmbeddedObjectListFieldDescribeBuilder();
    }

    public EmbeddedObjectListFieldDescribe build() {
        return embeddedObjectListFieldDescribe;
    }

    public EmbeddedObjectListFieldDescribeBuilder apiName(String apiName) {
        embeddedObjectListFieldDescribe.setApiName(apiName);
        return this;
    }

    public EmbeddedObjectListFieldDescribeBuilder defineType(String defineType) {
        embeddedObjectListFieldDescribe.setDefineType(defineType);
        return this;
    }

    public EmbeddedObjectListFieldDescribeBuilder required(boolean required) {
        embeddedObjectListFieldDescribe.setRequired(required);
        return this;
    }

    public EmbeddedObjectListFieldDescribeBuilder unique(boolean unique) {
        embeddedObjectListFieldDescribe.setUnique(unique);
        return this;
    }

    public EmbeddedObjectListFieldDescribeBuilder label(String label) {
        embeddedObjectListFieldDescribe.setLabel(label);
        return this;
    }
}

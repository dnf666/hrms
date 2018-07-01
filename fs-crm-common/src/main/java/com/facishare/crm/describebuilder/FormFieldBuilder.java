package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.ui.layout.FormField;

public class FormFieldBuilder {
    private FormField formField;

    private FormFieldBuilder() {
        formField = new FormField();
    }

    public static FormFieldBuilder builder() {
        return new FormFieldBuilder();
    }

    public FormField build() {
        return formField;
    }

    public FormFieldBuilder fieldName(String fieldName) {
        formField.setFieldName(fieldName);
        return this;
    }

    public FormFieldBuilder renderType(String renderType) {
        formField.setRenderType(renderType);
        return this;
    }

    public FormFieldBuilder readOnly(boolean readOnly) {
        formField.setReadOnly(readOnly);
        return this;
    }

    public FormFieldBuilder required(boolean required) {
        formField.setRequired(required);
        return this;
    }

}

package com.facishare.crm.describebuilder;

import java.util.List;

import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;

public class FieldSectionBuilder {
    private FieldSection fieldSection;

    private FieldSectionBuilder() {
        fieldSection = new FieldSection();
    }

    public static FieldSectionBuilder builder() {
        return new FieldSectionBuilder();
    }

    public FieldSection build() {
        return fieldSection;
    }

    public FieldSectionBuilder name(String name) {
        fieldSection.setName(name);
        return this;
    }

    public FieldSectionBuilder header(String header) {
        fieldSection.setHeader(header);
        return this;
    }

    public FieldSectionBuilder showHeader(boolean showHeader) {
        fieldSection.setShowHeader(showHeader);
        return this;
    }

    public FieldSectionBuilder fields(List<IFormField> formFields) {
        fieldSection.setFields(formFields);
        return this;
    }

}

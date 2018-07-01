package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.SelectOption;

public class SelectOptionBuilder {
    private SelectOption selectOption;

    private SelectOptionBuilder() {
        this.selectOption = new SelectOption();
        selectOption.setNotUsable(false);
    }

    public static SelectOptionBuilder builder() {
        return new SelectOptionBuilder();
    }

    public SelectOption build() {
        return selectOption;
    }

    public SelectOptionBuilder value(String value) {
        selectOption.setValue(value);
        return this;
    }

    public SelectOptionBuilder label(String label) {
        selectOption.setLabel(label);
        return this;
    }
}

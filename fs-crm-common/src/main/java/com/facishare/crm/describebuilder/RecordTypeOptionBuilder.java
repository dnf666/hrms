package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.RecordTypeOption;

public class RecordTypeOptionBuilder {
    private RecordTypeOption recordTypeOption;

    private RecordTypeOptionBuilder() {
        recordTypeOption = new RecordTypeOption();
        recordTypeOption.setIsActive(true);
    }

    public static RecordTypeOptionBuilder builder() {
        return new RecordTypeOptionBuilder();
    }

    public RecordTypeOption build() {
        return recordTypeOption;
    }

    public RecordTypeOptionBuilder apiName(String apiName) {
        recordTypeOption.setApiName(apiName);
        return this;
    }

    public RecordTypeOptionBuilder label(String label) {
        recordTypeOption.setLabel(label);
        return this;
    }

}

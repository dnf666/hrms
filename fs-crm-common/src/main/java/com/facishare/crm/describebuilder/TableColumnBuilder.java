package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.ui.layout.TableColumn;

public class TableColumnBuilder {
    private TableColumn tableColumn;

    private TableColumnBuilder() {
        tableColumn = new TableColumn();
    }

    public static TableColumnBuilder builder() {
        return new TableColumnBuilder();
    }

    public TableColumn build() {
        return tableColumn;
    }

    public TableColumnBuilder name(String name) {
        tableColumn.setName(name);
        return this;
    }

    public TableColumnBuilder lableName(String lableName) {
        tableColumn.setLabelName(lableName);
        return this;
    }

    public TableColumnBuilder renderType(String renderType) {
        tableColumn.setRenderType(renderType);
        return this;
    }
}

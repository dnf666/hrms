package com.facishare.crm.describebuilder;

import java.util.List;

import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ITableColumn;

public class TableComponentBuilder {
    private TableComponent tableComponent;

    private TableComponentBuilder() {
        tableComponent = new TableComponent();
    }

    public static TableComponentBuilder builder() {
        return new TableComponentBuilder();
    }

    public TableComponent build() {
        return tableComponent;
    }

    public TableComponentBuilder includeFields(List<ITableColumn> tableColumns) {
        tableComponent.setIncludeFields(tableColumns);
        return this;
    }

    public TableComponentBuilder refObjectApiName(String refObjectApiName) {
        tableComponent.setRefObjectApiName(refObjectApiName);
        return this;
    }

    public TableComponentBuilder buttons(List<IButton> buttons) {
        tableComponent.setButtons(buttons);
        return this;
    }
}

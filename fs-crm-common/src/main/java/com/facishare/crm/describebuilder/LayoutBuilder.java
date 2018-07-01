package com.facishare.crm.describebuilder;

import java.util.Date;
import java.util.List;

import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IComponent;

public class LayoutBuilder {
    private Layout layout;

    private LayoutBuilder() {
        layout = new Layout();
        layout.setDeleted(false);
        layout.setPackage("CRM");
        layout.setCreateTime(System.currentTimeMillis());
    }

    public static LayoutBuilder builder() {
        return new LayoutBuilder();
    }

    public Layout build() {
        return layout;
    }

    public LayoutBuilder components(List<IComponent> componentList) {
        layout.setComponents(componentList);
        return this;
    }

    public LayoutBuilder name(String apiName) {
        layout.setName(apiName);
        return this;
    }

    public LayoutBuilder refObjectApiName(String refObjectApiName) {
        layout.setRefObjectApiName(refObjectApiName);
        return this;
    }

    public LayoutBuilder displayName(String displayName) {
        layout.setDisplayName(displayName);
        return this;
    }

    public LayoutBuilder tenantId(String tenantId) {
        layout.setTenantId(tenantId);
        return this;
    }

    public LayoutBuilder createBy(String createBy) {
        layout.setCreatedBy(createBy);
        return this;
    }

    public LayoutBuilder isDefault(boolean isDefualt) {
        layout.setIsDefault(isDefualt);
        return this;
    }

    public LayoutBuilder layoutType(String layoutType) {
        layout.setLayoutType(layoutType);
        return this;
    }

    public LayoutBuilder isShowFieldName(boolean isShowFieldName) {
        layout.setIsShowFieldname(isShowFieldName);
        return this;
    }

    public LayoutBuilder agentType(String agentType) {
        layout.setAgentType(agentType);
        return this;
    }
}

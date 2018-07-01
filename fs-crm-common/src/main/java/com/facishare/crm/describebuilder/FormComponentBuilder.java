package com.facishare.crm.describebuilder;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Lists;

public class FormComponentBuilder {
    private FormComponent formComponent;

    private FormComponentBuilder() {
        formComponent = new FormComponent();
    }

    public static FormComponentBuilder builder() {
        return new FormComponentBuilder();
    }

    public FormComponent build() {
        return formComponent;
    }

    public FormComponentBuilder name(String name) {
        formComponent.setName(name);
        return this;
    }

    public FormComponentBuilder fieldSections(List<IFieldSection> fieldSections) {
        if (!includeSystemFieldSection(fieldSections)) {
            List<IFormField> systemFormFields = Lists.newArrayList();
            systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.CreateBy.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(false).readOnly(true).build());
            systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.CreateTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(false).readOnly(true).build());
            systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LastModifiedBy.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(false).readOnly(true).build());
            systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LastModifiedTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(false).readOnly(true).build());
            fieldSections.add(FieldSectionBuilder.builder().showHeader(true).name(LayoutConstants.SYSTEM_FIELD_SECTION_API_NAME).fields(systemFormFields).header(LayoutConstants.SYSTEM_FIELD_SECTION_DISPLAY_NAME).build());
        }
        formComponent.setFieldSections(fieldSections);
        return this;
    }

    private boolean includeSystemFieldSection(List<IFieldSection> fieldSections) {
        for (IFieldSection fieldSection : fieldSections) {
            if (LayoutConstants.SYSTEM_FIELD_SECTION_API_NAME.equals(fieldSection.getName())) {
                return true;
            }
        }
        return false;
    }

    public FormComponentBuilder buttons(List<IButton> buttons) {
        if (CollectionUtils.isNotEmpty(buttons)) {
            formComponent.setButtons(buttons);
        }
        return this;
    }
}

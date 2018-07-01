package com.facishare.crm.customeraccount.predefine.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerAccountDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result after(Arg arg, Result result) {
        String layoutType = arg.getLayout_type();
        Layout layout = new Layout(result.getLayout());
        if (!StringUtil.isNullOrEmpty(layoutType)) {
            try {
                List<IComponent> components = layout.getComponents();
                if (UdobjConstants.APP_SCENE_DETAIL.equals(layoutType)) {
                    for (IComponent component : components) {
                        if (component instanceof IFormComponent) {
                            IFormComponent formComponent = (IFormComponent) component;
                            if (LayoutConstants.HEADER_API_NAME.equals(formComponent.getName())) {
                                List<IFieldSection> fieldSection = formComponent.getFieldSections();
                                List<IFormField> formFields = fieldSection.get(0).getFields();
                                IFormField formField = new FormField();
                                formField.setFieldName(CustomerAccountConstants.Field.Customer.apiName);
                                formField.setReadOnly(true);
                                formField.setRenderType("master_detail");
                                formField.setRequired(true);
                                IFormField formField1 = new FormField();
                                formField1.setFieldName(SystemConstants.Field.LifeStatus.apiName);
                                formField1.setReadOnly(true);
                                formField1.setRenderType("text");
                                formField1.setRequired(true);
                                formFields.add(formField);
                                formFields.add(formField1);
                                fieldSection.get(0).setFields(formFields);
                            }
                        }
                    }
                }
                if (UdobjConstants.LAYOUT_TYPE_EDIT.equals(layoutType)) {
                    if (!components.isEmpty()) {
                        FormComponent fc = (FormComponent) components.get(0);
                        List<IFieldSection> fields = fc.getFieldSections();
                        for (IFieldSection fieldSection : fields) {
                            List<IFormField> formFields = fieldSection.getFields();
                            Iterator iterator = formFields.iterator();
                            while (iterator.hasNext()) {
                                IFormField formField = (IFormField) iterator.next();
                                if (CustomerAccountConstants.Field.PrepayBalance.getApiName().equals(formField.getFieldName())) {
                                    formField.setReadOnly(true);
                                } else if (CustomerAccountConstants.Field.RebateBalance.getApiName().equals(formField.getFieldName())) {
                                    formField.setReadOnly(true);
                                }
                            }

                            if (fieldSection.getName().equals(LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {//"base_field_section__c"
                                Set<String> fieldNames = formFields.stream().map(IFormField::getFieldName).collect(Collectors.toSet());
                                if (!fieldNames.contains(CustomerAccountConstants.Field.Customer.apiName)) {
                                    IFormField customerFormField = new FormField();
                                    customerFormField.setReadOnly(true);
                                    customerFormField.setRenderType("master_detail");
                                    customerFormField.setFieldName(CustomerAccountConstants.Field.Customer.apiName);
                                    customerFormField.setRequired(true);
                                    formFields.add(0, customerFormField);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("arg=" + arg, e);
            }
        }
        return result;
    }

}

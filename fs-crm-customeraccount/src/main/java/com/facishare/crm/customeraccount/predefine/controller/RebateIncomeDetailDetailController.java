package com.facishare.crm.customeraccount.predefine.controller;

import java.util.List;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailDetailController extends StandardDetailController {
    List<IFormField> rebateHeader = Lists.newArrayList();

    @Override
    protected Result after(StandardDetailController.Arg arg, StandardDetailController.Result result) {
        setRebateIncomeHeader();
        Result result1 = super.after(arg, result);
        try {
            Layout layout = new Layout(result1.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {//"顶部信息"
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        fieldSections.get(0).setFields(rebateHeader);
                    }
                    if (component.getName().equals(LayoutConstants.DETAIL_INFO)) {//"detailInfo"
                        List<IComponent> childComponents = ((GroupComponent) component).getChildComponents();
                        for (IComponent childComponent : childComponents) {
                            FormComponent formComponent = ((FormComponent) childComponent);
                            if (formComponent.getName().equals(LayoutConstants.FORM_COMPONENT_API_NAME)) {//"form_component"
                                List<IFieldSection> fieldSections = formComponent.getFieldSections();
                                for (IFieldSection fieldSection : fieldSections) {
                                    if (fieldSection.getName().equals(LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {//base_field_section__c
                                        List<IFormField> formFields = fieldSection.getFields();
                                        formFields.removeIf(iFormField -> iFormField.getFieldName().equals(RebateIncomeDetailConstants.Field.CustomerAccount.apiName));
                                        fieldSection.setFields(formFields);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.error("layout getComponent error:{}", e);
        }
        return result1;
    }

    private void setRebateIncomeHeader() {
        IFormField formField = new FormField();
        formField.setFieldName(RebateIncomeDetailConstants.Field.Amount.apiName);
        formField.setReadOnly(true);
        formField.setRenderType("currency");
        formField.setRequired(true);
        IFormField formField4 = new FormField();
        formField4.setFieldName(RebateIncomeDetailConstants.Field.Customer.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType("object_reference");
        formField4.setRequired(true);
        IFormField formField1 = new FormField();
        formField1.setFieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType("currency");
        formField1.setRequired(false);
        IFormField formField3 = new FormField();
        formField3.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType("select_one");
        formField3.setRequired(true);
        //rebateHeader.add(formField4);
        rebateHeader.add(formField);
        rebateHeader.add(formField1);
        rebateHeader.add(formField3);

    }
}

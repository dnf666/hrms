package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;

import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;

import java.util.List;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * @author cqx
 * @date 2018/3/22 11:06
 */
public class ContactDescribeLayoutController extends ContactPartnerDescribeLayoutFilterController {
    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if (arg.getLayout_type() == null) {
            return;
        }

        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                removeSystemInfoFieldSection();
                break;
            case LAYOUT_TYPE_ADD:
                removeSystemInfoFieldSection();
                break;
            default:
                break;
        }
    }

    //删除系统字段
    private void removeSystemInfoFieldSection() {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        if (formComponent == null) {
            return;
        }
        List<IFieldSection> tmpFieldSections = formComponent.getFieldSections();
        for (IFieldSection fieldSection : tmpFieldSections) {

            String fieldSectionApiName = fieldSection.get("api_name", String.class);
            if (!"system_form_field_generate_by_UDObjectServer__c".equals(fieldSectionApiName)) {
                if ("base_field_section__c".equals(fieldSectionApiName)) {
                    List<IFormField> fields = fieldSection.getFields();
                    fields.removeIf(field -> "lock_status".equals(field.getFieldName()) || "owner".equals(field.getFieldName()));
                    fieldSection.setFields(fields);
                }
                fieldSections.add(fieldSection);
            }
        }
        formComponent.setFieldSections(fieldSections);
    }

}

package com.facishare.crm.promotion.predefine.controller;

import java.util.List;
import java.util.Set;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Sets;

public class PromotionListHeaderController extends StandardListHeaderController {
    public static Set<String> fielsToRemove = Sets.newHashSet(SystemConstants.Field.ExtendObjDataId.apiName, PromotionConstants.Field.CustomerRange.apiName);

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        List<DocumentBaseEntity> fieldList = result.getFieldList();
        if (CollectionUtils.isNotEmpty(fieldList)) {
            fieldList.removeIf(documentBaseEntity -> documentBaseEntity.keySet().contains(PromotionConstants.Field.CustomerRange.apiName) || documentBaseEntity.keySet().contains(SystemConstants.Field.ExtendObjDataId.apiName));
        } else {
            LayoutDocument layoutDocument = result.getLayout();
            ILayout layout = layoutDocument.toLayout();
            try {
                List<IComponent> components = layout.getComponents();
                if (CollectionUtils.isNotEmpty(components)) {
                    components.forEach(component -> {
                        if (LayoutConstants.FORM.equals(component.getType()) && component instanceof FormComponent) {
                            FormComponent formComponent = (FormComponent) component;
                            List<IFieldSection> fieldSections = formComponent.getFieldSections();
                            if (CollectionUtils.isNotEmpty(fieldSections)) {
                                for (IFieldSection fieldSection : fieldSections) {
                                    List<IFormField> formFields = fieldSection.getFields();
                                    if (CollectionUtils.isEmpty(formFields)) {
                                        continue;
                                    }
                                    formFields.removeIf(formField -> fielsToRemove.contains(formField.getFieldName()));
                                    fieldSection.setFields(formFields);
                                }
                            }
                            formComponent.setFieldSections(fieldSections);
                        }
                    });
                }
            } catch (MetadataServiceException e) {
                log.warn("", e);
            }
        }
        return result;
    }
}

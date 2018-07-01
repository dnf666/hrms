package com.facishare.crm.customeraccount.predefine.controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateOutcomeDetailDetailController extends StandardDetailController {
    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {//顶部信息
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        if (CollectionUtils.isNotEmpty(fieldSections) && Objects.nonNull(fieldSections.get(0))) {
                            Set<String> fieldNameSets = fieldSections.get(0).getFields().stream().map(IFormField::getFieldName).collect(Collectors.toSet());
                            if (!fieldNameSets.contains(SystemConstants.Field.LifeStatus.apiName)) {
                                IFormField formField = new FormField();
                                formField.setFieldName(SystemConstants.Field.LifeStatus.apiName);
                                formField.setReadOnly(true);
                                formField.setRequired(false);
                                formField.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
                                fieldSections.get(0).addFields(Lists.newArrayList(formField));
                            }
                        }
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.error("layout getComponent error:{}", e);
        }
        return result;
    }
}

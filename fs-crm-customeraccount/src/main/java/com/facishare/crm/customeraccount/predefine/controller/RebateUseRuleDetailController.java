package com.facishare.crm.customeraccount.predefine.controller;

import java.util.List;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.manager.CustomerRangeManager;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateUseRuleDetailController extends StandardDetailController {
    private CustomerRangeManager customerRangeManager;

    @Override
    protected void before(Arg arg) {
        customerRangeManager = SpringUtil.getContext().getBean(CustomerRangeManager.class);
        super.before(arg);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        customerRangeManager.packData(controllerContext.getUser(), result.getData(), RebateUseRuleConstants.Field.CustomerRange.apiName);
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {//"顶部信息"
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        fieldSections.get(0).setFields(getRebateUseRuleHeader());
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.error("layout getComponent error:{}", e);
        }
        return result;
    }

    private List<IFormField> getRebateUseRuleHeader() {
        List<IFormField> headerField = Lists.newArrayList();
        IFormField formField1 = new FormField();
        formField1.setFieldName(RebateUseRuleConstants.Field.Name.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.Text.renderType);
        formField1.setRequired(true);
        IFormField formField2 = new FormField();
        formField2.setFieldName(RebateUseRuleConstants.Field.Status.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.TrueOrFalse.renderType);
        formField2.setRequired(true);
        IFormField formField3 = new FormField();
        formField3.setFieldName(RebateUseRuleConstants.Field.StartTime.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField3.setRequired(false);
        IFormField formField4 = new FormField();
        formField4.setFieldName(RebateUseRuleConstants.Field.EndTime.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField4.setRequired(true);
        IFormField formField5 = new FormField();
        formField5.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField5.setRequired(true);
        headerField.add(formField1);
        headerField.add(formField2);
        headerField.add(formField3);
        headerField.add(formField4);
        headerField.add(formField5);
        return headerField;
    }
}

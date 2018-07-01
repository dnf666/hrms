package com.facishare.crm.customeraccount.predefine.controller;

import java.util.Iterator;
import java.util.List;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.BaseListController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailListController extends StandardListController {

    @Override
    protected Result after(StandardListController.Arg arg, BaseListController.Result result) {
        BaseListController.Result result1 = super.after(arg, result);
        List<LayoutDocument> layList = result1.getListLayouts();
        for (LayoutDocument layoutDocument : layList) {
            Layout layout = new Layout(layoutDocument);
            List<IComponent> components = null;
            components = getComponents(layout, components);
            filterExtendObjDataId(components);
        }

        return result1;

    }

    private void filterExtendObjDataId(List<IComponent> components) {
        for (IComponent component : components) {
            if (component instanceof IFormComponent) {
                FormComponent fc = (FormComponent) component;
                List<IFieldSection> fields = fc.getFieldSections();
                for (IFieldSection field : fields) {
                    if (LayoutConstants.BASE_FIELD_SECTION_API_NAME.equals(field.getName())) {//base_field_section__c
                        List<IFormField> formFields = field.getFields();
                        Iterator iterator = formFields.iterator();
                        while (iterator.hasNext()) {
                            IFormField formField1 = (IFormField) iterator.next();
                            if ("extend_obj_data_id".equals(formField1.getFieldName())) {
                                log.info("RebateIncomeDetailListController extend_obj_data_id remove log!");
                                iterator.remove();
                            }
                        }
                        field.setFields(formFields);
                    }
                }
            }
        }
    }

    private List<IComponent> getComponents(Layout layout, List<IComponent> components) {
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.error("prepayDetailDescribeLayoutController error", e);
        }
        return components;
    }

}

package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by luxin on 2018/1/16.
 */
@Component
public class ProductDescribeLayoutController extends SFADescribeLayoutController {


    @Override
    protected void handleLayout(Arg arg, Result result) {
        if (arg.getLayout_type() == null) {
            return;
        }

        User user = getControllerContext().getUser();
        ILayout layout = new Layout(result.getLayout());

        try {
            if (CollectionUtils.empty(layout.getComponents())) {
                return;
            }
        } catch (MetadataServiceException e) {
            throw new MetaDataBusinessException(e.getMessage());
        }
        IObjectDescribe describe = result.getObjectDescribe().toObjectDescribe();

        formComponent = (FormComponent) LayoutExt.of(layout).getFormComponent().get().getFormComponent();

        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                removeSystemInfoFieldSection();
                PreDefLayoutUtil.specialDealAccountObjAccountName(describe.getApiName(), this.serviceFacade, formComponent,
                        user.getTenantId());
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_EDIT);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_EDIT);
                break;
            case LAYOUT_TYPE_ADD:
                removeSystemInfoFieldSection();
                PreDefLayoutUtil.specialDealOldObjRequiredReadOnly(formComponent, describe);
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_ADD);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_ADD);
                break;
            default:
                break;
        }
    }

    //删除系统字段
    private void removeSystemInfoFieldSection() {
        List<IFieldSection> fieldSections = Lists.newArrayList();
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

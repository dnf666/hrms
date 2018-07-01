package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;

import java.util.List;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by zhaopx on 2017/11/14.
 */
public class PriceBookProductDescribeLayoutController extends StandardDescribeLayoutController {
    @Override
    protected Result after(Arg arg, Result result) {
        ILayout layout = new Layout(result.getLayout());
        try {
            if (CollectionUtils.empty(layout.getComponents())) {
                return super.after(arg, result);
            }
        } catch (MetadataServiceException e) {
            throw new MetaDataBusinessException(e.getMessage());
        }
        FormComponent formComponent = (FormComponent) LayoutExt.of(layout).getFormComponent().get().getFormComponent();
        List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                    List<IFormField> fields = fieldSection.getFields();
                    for (IFormField formField : fields) {
                        if ("product_id".equals(formField.getFieldName())) {
                            formField.setReadOnly(Boolean.TRUE);
                        }
                    }
                    fieldSection.setFields(fields);
                    fieldSectionsResult.add(fieldSection);
                }
                formComponent.setFieldSections(fieldSectionsResult);
                break;
            case LAYOUT_TYPE_ADD:
                for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                    List<IFormField> fields = fieldSection.getFields();
                    List<IFormField> removeFields = Lists.newArrayList();
                    for (IFormField formField : fields) {
                        if ("name".equals(formField.getFieldName())) {
                            removeFields.add(formField);
                        }
                    }
                    fields.removeAll(removeFields);
                    fieldSection.setFields(fields);
                    fieldSectionsResult.add(fieldSection);
                }
                formComponent.setFieldSections(fieldSectionsResult);
                break;
            default:
                break;
        }
        result.setLayout(LayoutDocument.of(layout));
        return result;
    }

}

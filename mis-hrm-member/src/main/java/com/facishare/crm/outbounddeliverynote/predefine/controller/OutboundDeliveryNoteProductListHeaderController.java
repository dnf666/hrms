package com.facishare.crm.outbounddeliverynote.predefine.controller;

import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/22
 */
@Slf4j
public class OutboundDeliveryNoteProductListHeaderController extends StandardListHeaderController {

    @Override
    protected StandardListHeaderController.Result after(StandardListHeaderController.Arg arg, StandardListHeaderController.Result result) {
        result = super.after(arg, result);

        Layout layout = new Layout(result.getLayout());

        //删除冗余字段：出库单产品编号和出库单产品id
        delRedundancyFields(result, layout);

        //清空出库单产品按钮
        delRedundancyButtons(result, layout);
        return result;
    }

    private void delRedundancyFields(Result result, Layout layout) {
        try {
            List<IComponent> components = layout.getComponents();
            if (CollectionUtils.isEmpty(components)) {
                return;
            }
            IFormComponent formComponent = (IFormComponent)components.get(0);
            if (Objects.isNull(formComponent)) {
                return;
            }
            List<IFieldSection> fieldSection = formComponent.getFieldSections();
            if (CollectionUtils.isEmpty(fieldSection)) {
                return;
            }
            List<IFormField> formFields = fieldSection.get(0).getFields();
            if (CollectionUtils.isEmpty(formFields)) {
                return;
            }
            fieldSection.get(0).setFields(formFields.stream().filter(formField ->
                    !formField.getFieldName().equals(OutboundDeliveryNoteProductConstants.Field.Name.apiName) &&
                            !formField.getFieldName().equals(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName)
            ).collect(Collectors.toList()));
            formComponent.setFieldSections(fieldSection);
            components.set(0, formComponent);
            layout.setComponents(components);
            result.setLayout(LayoutDocument.of(layout));
        } catch (MetadataServiceException e) {
            log.error("layout getComponent failed! result[{}], layout[{}]", result, layout, e);
        }
    }

    private void delRedundancyButtons(Result result, Layout layout) {
        List<IButton> buttons = Lists.newArrayList();
        layout.setButtons(buttons);
        result.setLayout(LayoutDocument.of(layout));
    }
}

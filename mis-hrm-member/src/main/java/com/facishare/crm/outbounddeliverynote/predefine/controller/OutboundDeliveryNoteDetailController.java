package com.facishare.crm.outbounddeliverynote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author linchf
 * @date 2018/3/16
 */
@Slf4j
public class OutboundDeliveryNoteDetailController extends StandardDetailController {

    private List<IFormField> outboundDeliveryNoteHeader = Lists.newArrayList();
    private static String OUTBOUND_NOTE_PRODUCT_MD_GROUP_COMPONENT = OutboundDeliveryNoteProductConstants.API_NAME  + "_md_group_component";
    private static String CHILD_COMPONENTS = "child_components";
    private static String BUTTIONS = "buttons";
    private static String ACTION = "action";


    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        setOutboundDeliveryNoteHeader();

        filterField(result);

        //修改入库单顶部信息
        modifyFieldSection(result);

        //隐藏编辑和作废按钮
        delNoteButtons(result);

        //隐藏出库单产品新建按钮
        delRelatedProductObjectAddButton(result);
        return result;
    }

    private void modifyFieldSection(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!CollectionUtils.isEmpty(components)) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        if (!CollectionUtils.isEmpty(fieldSections)) {
                            fieldSections.get(0).setFields(outboundDeliveryNoteHeader);
                        }
                    }
                }
                layout.setComponents(components);
            }
            result.setLayout(LayoutDocument.of(layout));

        } catch (MetadataServiceException e) {
            log.error("layout getComponent failed, result[{}]", result, e);
        }
    }

    private void filterField(Result result) {
        String recordType = result.getData().toObjectData().getRecordType();
        Map<String, Object> fields = (Map<String, Object>) result.getDescribe().get("fields");

        if (Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)) {
            fields.remove(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName);
            fields.remove(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName);
        }
        if (Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.RequisitionOutbound.apiName)) {
            fields.remove(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName);
        }
        if (Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.SalesOutbound.apiName)) {
            fields.remove(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName);
        }
    }

    private void delNoteButtons(Result result) {
        Layout layout = new Layout(result.getLayout());
        String lifeStatus = data.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String recordType = data.getRecordType();
        List<IButton> buttons = layout.getButtons();
        if (CollectionUtils.isEmpty(buttons)) {
            return;
        }

        List<IButton> newButtons = Lists.newArrayList();
        for (IButton button : buttons) {
            if (ObjectAction.UPDATE.getActionCode().equals(button.getAction())
                    || ObjectAction.INVALID.getActionCode().equals(button.getAction())) {

                if (Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)) {
                    newButtons.add(button);
                }
            } else {
                newButtons.add(button);
            }
        }
        layout.setButtons(newButtons);
        result.setLayout(LayoutDocument.of(layout));
    }

    private void delRelatedProductObjectAddButton(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> componentList = layout.getComponents();
            componentList.stream()
                    .filter(component -> OUTBOUND_NOTE_PRODUCT_MD_GROUP_COMPONENT.equals(component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        childComponents.stream().filter(childComponent -> Objects.equals(childComponent.get("ref_object_api_name"), OutboundDeliveryNoteProductConstants.API_NAME))
                                .forEach(childComponent -> {
                                    // 删除childComponents里的新建Button（IOS、WEB用的这个属性控制的新建按钮）
                                    List<Map> subChildComponents = (List) childComponent.get(CHILD_COMPONENTS);
                                    if (!org.springframework.util.CollectionUtils.isEmpty(subChildComponents)) {
                                        subChildComponents.forEach(relatedListChildComponent -> {
                                            List<Map> subChildComponentsButtons = (List) relatedListChildComponent.get(BUTTIONS);
                                            if (!org.springframework.util.CollectionUtils.isEmpty(subChildComponentsButtons)) {
                                                subChildComponentsButtons.removeIf(btnMap -> btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                                            }
                                        });
                                    }

                                    // 删除buttons里的新建（安卓端用的这个属性控制的新建按钮）
                                    List<Map> buttons = (List) childComponent.get(BUTTIONS);
                                    if (!org.springframework.util.CollectionUtils.isEmpty(buttons)) {
                                        buttons.removeIf(btnMap -> btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                                    }
                                });
                    });
        } catch (MetadataServiceException e) {
            log.error("delRelatedProductObjectAddButton error. result[{}]", result, e);
        }
    }

    private void setOutboundDeliveryNoteHeader() {
        IFormField formField = new FormField();
        formField.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField.setReadOnly(true);
        formField.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField.setRequired(true);

        IFormField formField2 = new FormField();
        formField2.setFieldName(OutboundDeliveryNoteConstants.Field.Outbound_Date.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField2.setRequired(true);

        IFormField formField3 = new FormField();
        formField3.setFieldName(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField3.setRequired(false);

        IFormField formField4 = new FormField();
        formField4.setFieldName(OutboundDeliveryNoteConstants.Field.Warehouse.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
        formField4.setRequired(false);

        IFormField formField5 = new FormField();
        formField5.setFieldName(SystemConstants.Field.Owner.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(SystemConstants.RenderType.Employee.renderType);
        formField5.setRequired(true);

        IFormField formField6 = new FormField();
        formField6.setFieldName(SystemConstants.Field.OwnerDepartment.apiName);
        formField6.setReadOnly(true);
        formField6.setRenderType(SystemConstants.RenderType.Text.renderType);
        formField6.setRequired(true);


        outboundDeliveryNoteHeader.add(formField);
        outboundDeliveryNoteHeader.add(formField2);
        outboundDeliveryNoteHeader.add(formField3);
        outboundDeliveryNoteHeader.add(formField4);
        outboundDeliveryNoteHeader.add(formField5);
        outboundDeliveryNoteHeader.add(formField6);
    }
}

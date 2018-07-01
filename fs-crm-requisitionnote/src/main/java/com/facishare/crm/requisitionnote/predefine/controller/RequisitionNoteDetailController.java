package com.facishare.crm.requisitionnote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
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

@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteDetailController extends StandardDetailController {
    private static String REQUISITION_NOTE_PRODUCT_MD_GROUP_COMPONENT = RequisitionNoteProductConstants.API_NAME  + "_md_group_component";
    private List<IFormField> formFields = Lists.newArrayList();
    private static String CHILD_COMPONENTS = "child_components";
    private static String BUTTIONS = "buttons";
    private static String ACTION = "action";

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        //设置调拨单头
        setRequisitionNoteHeader();
        modifyFieldSection(result);

        //隐藏调拨单单产品新建按钮
        delRelatedProductObjectAddButton(result);

        //确认收货按钮
        addInboundConfirmed(result);

        //已确认入库的调拨单去掉作废按钮
        delRequisitionNoteButton(result);

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
                            fieldSections.forEach(iFieldSection -> {
                                if(iFieldSection.getName().equals("detail")) {
                                    iFieldSection.setFields(formFields);
                                }
                            });
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

    private void delRelatedProductObjectAddButton(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> componentList = layout.getComponents();
            componentList.stream()
                    .filter(component -> REQUISITION_NOTE_PRODUCT_MD_GROUP_COMPONENT.equals(component.get(IComponent.NAME, String.class)) ||
                    Objects.equals("relatedObject", component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        childComponents.stream().filter(childComponent ->
                                Objects.equals(childComponent.get("ref_object_api_name"), RequisitionNoteProductConstants.API_NAME) ||
                                Objects.equals(childComponent.get("ref_object_api_name"), GoodsReceivedNoteConstants.API_NAME) ||
                                Objects.equals(childComponent.get("ref_object_api_name"), OutboundDeliveryNoteConstants.API_NAME))
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

    private void addInboundConfirmed(Result result) {
        IObjectData objectData = result.getData().toObjectData();

        if (!Objects.equals(objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class), SystemConstants.LifeStatus.Normal.value)) {
            return;
        }
        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons().isEmpty() ? Lists.newArrayList() : layout.getButtons();
        Boolean inboundConfirmed = ObjectDataExt.of(result.getData()).get(RequisitionNoteConstants.Field.InboundConfirmed.apiName, Boolean.class);
        layout.setButtons(addButtons(controllerContext.getUser(), buttons, inboundConfirmed));
        result.setLayout(LayoutDocument.of(layout));
    }

    /**
     * 如果未确认入库则在 "更多"里面添加"确认入库"按钮
     */
    private List<IButton> addButtons(User user, List<IButton> buttons, Boolean inboundConfirmed) {

        if (Objects.equals(inboundConfirmed, false)) {
            IButton inboundConfirmedButton = new Button();
            inboundConfirmedButton.setAction(RequisitionNoteConstants.Button.InboundConfirmed.apiName);
            inboundConfirmedButton.setActionType(IButton.ACTION_TYPE_DEFAULT);
            inboundConfirmedButton.setName("InboundConfirmed_button_default");
            inboundConfirmedButton.setLabel(RequisitionNoteConstants.Button.InboundConfirmed.label);
            buttons.add(5, inboundConfirmedButton);
        }

        return buttons;
    }

    private void delRequisitionNoteButton(Result result) {
        Boolean inboundConfirmed = ObjectDataExt.of(result.getData()).get(RequisitionNoteConstants.Field.InboundConfirmed.apiName, Boolean.class);

        if (Objects.equals(inboundConfirmed, true)) {
            Layout layout = new Layout(result.getLayout());
            List<IButton> buttons = layout.getButtons();

            if (org.springframework.util.CollectionUtils.isEmpty(buttons)) {
                return;
            }
            buttons.removeIf(button -> ObjectAction.INVALID.getActionCode().equals(button.getAction()));
            layout.setButtons(buttons);
            result.setLayout(LayoutDocument.of(layout));
        }
    }


    private void setRequisitionNoteHeader() {

        IFormField formField1 = new FormField();
        formField1.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField1.setRequired(true);

        IFormField formField2 = new FormField();
        formField2.setFieldName(RequisitionNoteConstants.Field.RequisitionDate.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField2.setRequired(true);

        IFormField formField3 = new FormField();
        formField3.setFieldName(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
        formField3.setRequired(false);

        IFormField formField4 = new FormField();
        formField4.setFieldName(RequisitionNoteConstants.Field.TransferInWarehouse.apiName);
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

        formFields.add(formField1);
        formFields.add(formField2);
        formFields.add(formField3);
        formFields.add(formField4);
        formFields.add(formField5);
        formFields.add(formField6);
    }

}

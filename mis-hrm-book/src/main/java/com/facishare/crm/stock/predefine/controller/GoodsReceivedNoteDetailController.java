package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.GoodsReceivedTypeEnum;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
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

@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteDetailController extends StandardDetailController {
    private List<IFormField> goodsReceivedNoteHeader = Lists.newArrayList();
    private static String GOODS_RECEIVED_NOTE_PRODUCT_MD_GROUP_COMPONENT = GoodsReceivedNoteProductConstants.API_NAME  + "_md_group_component";
    private static String CHILD_COMPONENTS = "child_components";
    private static String BUTTIONS = "buttons";
    private static String ACTION = "action";

    @Override
    protected Result after(Arg arg, Result result) {
        setGoodsReceivedNoteHeader();
        result = super.after(arg, result);

        //修改入库单顶部信息
        modifyFieldSection(result);

        //预设业务类型时隐藏调拨单编号
        delRequisitionNote(result);

        //调拨入库类型的入库单隐藏按钮
        delGoodsReceivedNoteButtons(result);

        //隐藏入库单产品新建按钮
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
                            fieldSections.forEach(iFieldSection -> {
                                if(iFieldSection.getName().equals("detail")) {
                                    iFieldSection.setFields(goodsReceivedNoteHeader);
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

    private void delRequisitionNote(Result result) {
        Map<String, Object> fields = (Map<String, Object>) result.getDescribe().get("fields");

        if (Objects.equals(data.getRecordType(), MultiRecordType.RECORD_TYPE_DEFAULT)) {
            fields.remove(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName);
        }
    }

    private void delGoodsReceivedNoteButtons(Result result) {

        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons();
        if (CollectionUtils.isEmpty(buttons)) {
            return;
        }

        if (Objects.equals(data.getRecordType(), GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName)) {
            buttons.removeIf(button -> ObjectAction.UPDATE.getActionCode().equals(button.getAction()) || ObjectAction.BULK_INVALID.getActionCode().equals(button.getAction()) ||
                    ObjectAction.INVALID.getActionCode().equals(button.getAction()) || ObjectAction.RECOVER.getActionCode().equals(button.getAction()) || ObjectAction.BULK_RECOVER.getActionCode().equals(button.getAction()));
        }

        layout.setButtons(buttons);
        result.setLayout(LayoutDocument.of(layout));
    }

    private void delRelatedProductObjectAddButton(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> componentList = layout.getComponents();

            if (CollectionUtils.isEmpty(componentList)) {
                return;
            }

            componentList.stream()
                    .filter(component -> GOODS_RECEIVED_NOTE_PRODUCT_MD_GROUP_COMPONENT.equals(component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        if (CollectionUtils.isEmpty(childComponents)) {
                            return;
                        }

                        childComponents.forEach(childComponent -> {
                            List<Map> relatedListChildComponents = (List) childComponent.get(CHILD_COMPONENTS);
                            if (CollectionUtils.isEmpty(relatedListChildComponents)) {
                                return;
                            }

                            relatedListChildComponents.forEach(relatedListChildComponent -> {
                                List<Map> relatedListChildButtonMap = (List) relatedListChildComponent.get(BUTTIONS);
                                if (CollectionUtils.isEmpty(relatedListChildButtonMap)) {
                                    return;
                                }

                                relatedListChildButtonMap.removeIf(btnMap -> Objects.nonNull(btnMap.get(ACTION)) && btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                            });
                        });
                    });
        } catch (MetadataServiceException e) {
            log.error("delRelatedProductObjectAddButton failed, result[{}]. ", result, e);
        }
    }

    private void setGoodsReceivedNoteHeader() {
        IFormField formField = new FormField();
        formField.setFieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName);
        formField.setReadOnly(true);
        formField.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField.setRequired(true);

        IFormField formField1 = new FormField();
        formField1.setFieldName(GoodsReceivedNoteConstants.Field.Warehouse.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
        formField1.setRequired(false);

        IFormField formField2 = new FormField();
        formField2.setFieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField2.setRequired(false);

        IFormField formField3 = new FormField();
        formField3.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField3.setRequired(true);

        IFormField formField4 = new FormField();
        formField4.setFieldName(SystemConstants.Field.CreateBy.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.Employee.renderType);
        formField4.setRequired(true);

        goodsReceivedNoteHeader.add(formField);
        goodsReceivedNoteHeader.add(formField1);
        goodsReceivedNoteHeader.add(formField2);
        goodsReceivedNoteHeader.add(formField3);
        goodsReceivedNoteHeader.add(formField4);
    }
}

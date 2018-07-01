package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
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
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by linchf on 2018/1/19.
 */
@Slf4j(topic = "stockAccess")
public class StockDetailController extends StandardDetailController {

    private static String RELATED_OBJECT = "relatedObject";
    private static String DELIVERY_NOTE_PRODUCT_API_NAME = "DeliveryNoteProductObj_stock_id_related_list";
    private static String OUTBOUND_DELIVERY_NOTE_PRODUCT_API_NAME = "OutboundDeliveryNoteProductObj_stock_id_related_list";
    private static String REQUISITION_NOTE_PRODUCT_API_NAME = "RequisitionNoteProductObj_stock_id_related_list";


    private static String CHILD_COMPONENTS = "child_components";
    private static String BUTTIONS = "buttons";
    private static String ACTION = "action";

    private StockManager stockManager = (StockManager) SpringUtil.getContext().getBean("stockManager");

    @Override
    protected Result after(StandardDetailController.Arg arg, StandardDetailController.Result result) {
        result = super.after(arg, result);

        //修改头部信息
        modifyHeaderInfo(arg, result);

        //删除新建按钮
        delRelatedProductObjectAddButton(result);

        //补充安全库存
        result.setData(stockManager.fillSafetyStock(controllerContext.getUser(), Arrays.asList(result.getData())).get(0));


        return result;
    }

    private void modifyHeaderInfo(Arg arg, Result result) {
        Layout layout = new Layout(result.getLayout());
        try {
            List<IComponent> components = layout.getComponents();

            if (!CollectionUtils.isEmpty(components)) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        if (!CollectionUtils.isEmpty(fieldSections)) {
                            fieldSections.get(0).setFields(getHeaderInfos());
                        }
                    }
                }
                layout.setComponents(components);
            }
            result.setLayout(LayoutDocument.of(layout));
        } catch (MetadataServiceException e) {
            log.warn("layout getComponent failed! arg[{}], result[{}]", arg, result, e);
        }
    }

    private void delRelatedProductObjectAddButton(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> componentList = layout.getComponents();
            if (CollectionUtils.isEmpty(componentList)) {
                return;
            }
            componentList.stream()
                    .filter(component -> RELATED_OBJECT.equals(component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        if (childComponents == null) {
                            return;
                        }
                        childComponents.forEach(childComponent -> {
                            if(DELIVERY_NOTE_PRODUCT_API_NAME.equals(childComponent.get(IComponent.NAME))
                                    || OUTBOUND_DELIVERY_NOTE_PRODUCT_API_NAME.equals(childComponent.get(IComponent.NAME))
                                    || REQUISITION_NOTE_PRODUCT_API_NAME.equals(childComponent.get(IComponent.NAME))) {
                                removeCreateButton(childComponent);
                            }
                        });
                    });
        } catch (MetadataServiceException e) {
            log.error("delRelatedProductObjectAddButton failed. result[{}]", result, e);
        }
    }

    private void removeCreateButton(Map childComponent) {
        List<Map> buttons = (List)childComponent.get(BUTTIONS);
        if (buttons == null) {
            return;
        }
        buttons.removeIf(btnMap -> {
            if (btnMap.get(ACTION) == null) {
                return false;
            }
            return Objects.equals(btnMap.get(ACTION).toString(), ObjectAction.CREATE.getActionCode());
        });
    }

    private List<IFormField> getHeaderInfos() {
        List<IFormField> formFieldList = Lists.newArrayList();

        IFormField formField1 = new FormField();
        formField1.setFieldName(StockConstants.Field.Product.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
        formField1.setRequired(true);

        IFormField formField2 = new FormField();
        formField2.setFieldName(StockConstants.Field.Specs.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.Quote.renderType);
        formField2.setRequired(true);

        IFormField formField3 = new FormField();
        formField3.setFieldName(StockConstants.Field.Warehouse.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
        formField3.setRequired(true);

        IFormField formField4 = new FormField();
        formField4.setFieldName(StockConstants.Field.AvailableStock.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.Number.renderType);
        formField4.setRequired(true);

        IFormField formField5 = new FormField();
        formField5.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField5.setRequired(true);

        formFieldList.add(formField1);
        formFieldList.add(formField2);
        formFieldList.add(formField3);
        formFieldList.add(formField4);
        formFieldList.add(formField5);
        return formFieldList;
    }
}

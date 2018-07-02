package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.manager.CustomerRangeManager;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
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
public class WareHouseDetailController extends StandardDetailController {
    private CustomerRangeManager customerRangeManager;

    private final String DILIVERY_NOTE_OBJ = "DeliveryNoteObj";
    private final String RETURN_GOODS_INVOICE_OBJ = "ReturnedGoodsInvoiceObj";
    private final String SALES_ORDER_OBJ = "SalesOrderObj";
    private final String CHILD_COMPONENTS = "child_components";
    private final String BUTTONS = "buttons";
    private final String ACTION = "action";

    @Override
    protected Result after(StandardDetailController.Arg arg, StandardDetailController.Result result) {
        result = super.after(arg, result);
        if (Objects.nonNull(result.getData())) {
            if (customerRangeManager == null) {
                customerRangeManager = SpringUtil.getContext().getBean(CustomerRangeManager.class);
            }
            customerRangeManager.packData(controllerContext.getUser(), result.getData(), WarehouseConstants.Field.Account_range.apiName);
        }
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            //重写头部
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

            //隐藏终端仓库新建按钮
            List<IButton> buttons = layout.getButtons();
            List<IButton> newButtons = Lists.newArrayList();
            if (controllerContext.getClientInfo() != null &&
                    (controllerContext.getClientInfo().contains(RequestContext.Android_CLIENT_INFO_PREFIX)
                            || controllerContext.getClientInfo().contains(RequestContext.IOS_CLIENT_INFO_PREFIX))) {
                for (IButton button : buttons) {
                    if (!button.getAction().equals("Edit")) {
                        newButtons.add(button);
                    }
                }
                layout.setButtons(newButtons);
            }

            List<IComponent> componentList = layout.getComponents();
            //移除发货单新建按钮
            componentList.stream().filter(component -> "relatedObject".equals(component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        if (childComponents == null) {
                            return;
                        }

                        childComponents.stream().filter(childComponent ->
                                    Objects.equals(childComponent.get("ref_object_api_name"), DILIVERY_NOTE_OBJ)
                                            || Objects.equals(childComponent.get("ref_object_api_name"), RETURN_GOODS_INVOICE_OBJ)
                                            || Objects.equals(childComponent.get("ref_object_api_name"), SALES_ORDER_OBJ))
                                .forEach(childComponent -> {
                                    List<Map> b = (List)childComponent.get(BUTTONS);
                                    if (b == null) {
                                        return;
                                    }
                                    b.removeIf(button -> {
                                        if (button.get(ACTION) == null) {
                                            return false;
                                        }
                                        return button.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode());
                                    });
                                });
                    });
            result.setLayout(LayoutDocument.of(layout));
        } catch (MetadataServiceException e) {
            log.warn("layout getComponent failed! arg[{}], result[{}]", arg, result, e);
        }


        return result;
    }

    private List<IFormField> getHeaderInfos() {
        List<IFormField> formFieldList = Lists.newArrayList();

        IFormField formField1 = new FormField();
        formField1.setFieldName(WarehouseConstants.Field.Number.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.Text.renderType);
        formField1.setRequired(true);

        IFormField formField2 = new FormField();
        formField2.setFieldName(WarehouseConstants.Field.Country.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.Country.renderType);
        formField2.setRequired(true);

        IFormField formField3 = new FormField();
        formField3.setFieldName(WarehouseConstants.Field.Province.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.Province.renderType);
        formField3.setRequired(true);

        IFormField formField4 = new FormField();
        formField4.setFieldName(WarehouseConstants.Field.City.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.City.renderType);
        formField4.setRequired(true);

        IFormField formField5 = new FormField();
        formField5.setFieldName(WarehouseConstants.Field.District.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(SystemConstants.RenderType.District.renderType);
        formField5.setRequired(true);

        IFormField formField6 = new FormField();
        formField6.setFieldName(WarehouseConstants.Field.Address.apiName);
        formField6.setReadOnly(true);
        formField6.setRenderType(SystemConstants.RenderType.Text.renderType);
        formField6.setRequired(true);

        IFormField formField7 = new FormField();
        formField7.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField7.setReadOnly(true);
        formField7.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField7.setRequired(true);

        IFormField formField8 = new FormField();
        formField8.setFieldName(WarehouseConstants.Field.Is_Default.apiName);
        formField8.setReadOnly(true);
        formField8.setRenderType(SystemConstants.RenderType.TrueOrFalse.renderType);
        formField8.setRequired(true);

        formFieldList.add(formField1);
        formFieldList.add(formField2);
        formFieldList.add(formField3);
        formFieldList.add(formField4);
        formFieldList.add(formField5);
        formFieldList.add(formField6);
        formFieldList.add(formField7);
        formFieldList.add(formField8);
        return formFieldList;
    }
}

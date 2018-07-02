package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货单详情页
 * Created by chenzs on 2018/1/8.
 */
@Slf4j
public class DeliveryNoteDetailController extends StandardDetailController {
    public static String DELIVERY_NOTE_PRODUCT_MD_GROUP_COMPONENT = DeliveryNoteProductObjConstants.API_NAME  + "_md_group_component";
    public static String CHILD_COMPONENTS = "child_components";
    public static String BUTTONS = "buttons";
    public static String ACTION = "action";

    private StockManager stockManager;
    private FunctionPrivilegeService functionPrivilegeService;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        this.stockManager = SpringUtil.getContext().getBean(StockManager.class);
        this.functionPrivilegeService = (FunctionPrivilegeService) SpringUtil.getContext().getBean("functionPrivilegeService");
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        //修改头部信息
        modifyFieldSections(arg, result);

        //"更多"里面添加按钮"查看物流"和"确认收货"
        addViewLogisticAndConfirmReceive(result);

        // 删除关联的发货单产品新建按钮
        delRelatedProductObjectAddButton(result);

        //已收货的发货单隐藏作废按钮
        delDeliveryNoteButtons(result);

        return result;
    }

    private void delRelatedProductObjectAddButton(Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> componentList = layout.getComponents();
            componentList.stream()
                    .filter(component -> DELIVERY_NOTE_PRODUCT_MD_GROUP_COMPONENT.equals(component.get(IComponent.NAME, String.class)))
                    .forEach(component -> {
                        List<Map> childComponents = (List) component.get(CHILD_COMPONENTS, ArrayList.class);
                        childComponents.forEach(childComponent -> {
                            // 删除childComponents里的新建Button（IOS、WEB用的这个属性控制的新建按钮）
                            List<Map> subChildComponents = (List) childComponent.get(CHILD_COMPONENTS);
                            if (!CollectionUtils.isEmpty(subChildComponents)) {
                                subChildComponents.forEach(relatedListChildComponent -> {
                                    List<Map> subChildComponentsButtons = (List) relatedListChildComponent.get(BUTTONS);
                                    if (!CollectionUtils.isEmpty(subChildComponentsButtons)) {
                                        subChildComponentsButtons.removeIf(btnMap -> btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                                    }
                                });
                            }

                            // 删除buttons里的新建（安卓端用的这个属性控制的新建按钮）
                            List<Map> buttons = (List) childComponent.get(BUTTONS);
                            if (!CollectionUtils.isEmpty(buttons)) {
                                buttons.removeIf(btnMap -> btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                            }
                        });
            });
        } catch (MetadataServiceException e) {
            log.error("delRelatedProductObjectAddButton error. result[{}]", result, e);
        }
    }

    private void addViewLogisticAndConfirmReceive(Result result) {
        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons().isEmpty() ? Lists.newArrayList() : layout.getButtons();
        String status = ObjectDataExt.of(result.getData()).get(DeliveryNoteObjConstants.Field.Status.getApiName(), String.class);
        layout.setButtons(addButtons(controllerContext.getUser(), buttons, status));
        result.setLayout(LayoutDocument.of(layout));
    }

    private void modifyFieldSections(Arg arg, Result result) {
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        fieldSections.get(0).setFields(getHeaderInfos());
                    }
                }
                layout.setComponents(components);
            }
            result.setLayout(LayoutDocument.of(layout));
        } catch (MetadataServiceException e) {
            log.warn("layout getComponent error:{}", arg, e);
        }
    }

    private List<IFormField> getHeaderInfos() {
        List<IFormField> formFieldList = Lists.newArrayList();

        IFormField formField = new FormField();
        formField.setFieldName(DeliveryNoteObjConstants.Field.SalesOrderId.apiName);
        formField.setReadOnly(true);
        formField.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.ObjectReference.renderType);
        formField.setRequired(true);
        formFieldList.add(formField);

        IFormField formField1 = new FormField();
        formField1.setFieldName(DeliveryNoteObjConstants.Field.DeliveryDate.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.Date.renderType);
        formField1.setRequired(true);
        formFieldList.add(formField1);

        IFormField formField2 = new FormField();
        formField2.setFieldName(DeliveryNoteObjConstants.Field.ExpressOrg.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.SelectOne.renderType);
        formField2.setRequired(true);
        formFieldList.add(formField2);

        IFormField formField3 = new FormField();
        formField3.setFieldName(DeliveryNoteObjConstants.Field.ExpressOrderId.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.Text.renderType);
        formField3.setRequired(true);
        formFieldList.add(formField3);

        //是否开启了库存
        boolean isStockEnable = stockManager.isStockEnable(this.getControllerContext().getTenantId());
        if (isStockEnable) {
            IFormField formField4 = new FormField();
            formField4.setFieldName(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName);
            formField4.setReadOnly(true);
            formField4.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.ObjectReference.renderType);
            formField4.setRequired(true);
            formFieldList.add(formField4);
        }

        IFormField formField5 = new FormField();
        formField5.setFieldName(DeliveryNoteObjConstants.Field.Status.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(com.facishare.crm.constants.SystemConstants.RenderType.SelectOne.renderType);
        formField5.setRequired(true);
        formFieldList.add(formField5);

        return formFieldList;
    }

    /**
     * 如果有"查看物流"权限（是"发货人员、订单管理员、订单财务、订货人员"角色）, "更多"里面添加"查看物流"按钮
     * 如果有"确认收货"权限 (是"发货人员"角色)，   并且发货单处于"已发货"状态, "更多"里面添加"确认收货"按钮
     */
    private List<IButton> addButtons(User user, List<IButton> buttons, String status) {
        log.info("DeliveryNoteDetailController, addButtons, user:{}, buttons:{}", user, buttons);

        //1、是否有"查看物流"、"确认收货"权限
        List<String> actionCodes = Lists.newArrayList(DeliveryNoteObjConstants.Button.ViewLogistics.apiName, DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName);
        Map<String, Boolean> funPrivilegeMap = functionPrivilegeService.funPrivilegeCheck(user, DeliveryNoteObjConstants.API_NAME, actionCodes);
        log.info("functionPrivilegeService.funPrivilegeCheck user:{}, objectApiName:{}, actionCodes:{}, result:{}", user, DeliveryNoteObjConstants.API_NAME, actionCodes, funPrivilegeMap);

        //2、如果有"查看物流"权限，添加按钮
        if (funPrivilegeMap.get(DeliveryNoteObjConstants.Button.ViewLogistics.apiName)) {
            IButton viewLogistics = new Button();
            viewLogistics.setAction("ViewLogistics");
            viewLogistics.setActionType(IButton.ACTION_TYPE_DEFAULT);  //于少博：应该用default
            viewLogistics.setName("ViewLogistics_button_default");
            viewLogistics.setLabel("查看物流");
            buttons.add(viewLogistics);
        }

        //3、如果有"确认收货"权限，并且"已发货"的情况，才有"确认收货"
        if (funPrivilegeMap.get(DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName) &&
                Objects.equals(status, DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus())) {
            IButton confirmReceipt = new Button();
            confirmReceipt.setAction("ConfirmReceipt");
            confirmReceipt.setActionType(IButton.ACTION_TYPE_DEFAULT);
            confirmReceipt.setName("ConfirmReceipt_button_default");
            confirmReceipt.setLabel("确认收货");
            buttons.add(confirmReceipt);
        }

        return buttons;
    }

    private void delDeliveryNoteButtons(Result result) {

        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons();
        if (CollectionUtils.isEmpty(buttons)) {
            return;
        }


        String status = data.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
        if (Objects.equals(status, DeliveryNoteObjStatusEnum.RECEIVED.getStatus())) {
            buttons.removeIf(button -> ObjectAction.INVALID.getActionCode().equals(button.getAction()));
        }

        layout.setButtons(buttons);
        result.setLayout(LayoutDocument.of(layout));
    }
}
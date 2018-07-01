package com.facishare.crm.promotion.predefine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.manager.CustomerRangeManager;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PromotionDetailController extends StandardDetailController {
    private CustomerRangeManager customerRangeManager;
    public static String PROMOTION_PRODUCT_MD_GROUP_COMPONENT = "PromotionProductObj_md_group_component";
    public static String PROMOTION_RULE_MD_GROUP_COMPONENT = "PromotionRuleObj_md_group_component";
    public static String CHILD_COMPONENTS = "child_components";
    public static String REF_OBJECT_API_NAME = "ref_object_api_name";
    public static String BUTTIONS = "buttons";
    public static String ACTION = "action";
    public static String RELATE = "relatedObject";

    @Override
    protected Result after(StandardDetailController.Arg arg, StandardDetailController.Result result) {
        result = super.after(arg, result);
        if (Objects.nonNull(result.getData())) {
            if (customerRangeManager == null) {
                customerRangeManager = SpringUtil.getContext().getBean(CustomerRangeManager.class);
            }
            customerRangeManager.packData(controllerContext.getUser(), result.getData(), PromotionConstants.Field.CustomerRange.apiName);
        }
        try {
            Layout layout = new Layout(result.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                //订单促销时，不显示促销产品Tab
                String recordType = ObjectDataExt.of(result.getData()).getRecordType();
                if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(recordType)) {
                    components.removeIf(component -> component.getName().equals(PROMOTION_PRODUCT_MD_GROUP_COMPONENT));
                }
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        fieldSections.get(0).setFields(getHeaderInfos());
                    }
                    if (RELATE.equals(component.getName())) {
                        GroupComponent groupComponent = (GroupComponent) component;
                        List<IComponent> childComponents = groupComponent.getChildComponents();
                        if (CollectionUtils.notEmpty(childComponents)) {
                            childComponents.stream().filter(childComponent -> {
                                String childComponentName = childComponent.get(REF_OBJECT_API_NAME, String.class);
                                return Utils.SALES_ORDER_API_NAME.equals(childComponentName) || Utils.SALES_ORDER_PRODUCT_API_NAME.equals(childComponentName);
                            }).forEach(childComponent -> {
                                RelatedObjectList relatedObjectList = (RelatedObjectList) childComponent;
                                relatedObjectList.setButtons(Lists.newArrayList());
                            });
                            //商品促销时，不显示销售订单Tab
                            if (PromotionRecordTypeEnum.ProductPromotion.apiName.equals(recordType)) {
                                childComponents.removeIf(childComponent -> Utils.SALES_ORDER_API_NAME.equals(childComponent.get(REF_OBJECT_API_NAME, String.class)));
                            }
                            //订单促销时，不显示促销产品Tab
                            if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(recordType)) {
                                childComponents.removeIf(childComponent -> Utils.SALES_ORDER_PRODUCT_API_NAME.equals(childComponent.get(REF_OBJECT_API_NAME, String.class)));
                            }
                            groupComponent.setChildComponents(childComponents);
                        }
                    }
                    //详情页，去掉促销产品和促销规则tab的新建按钮
                    if (Sets.newHashSet(PROMOTION_RULE_MD_GROUP_COMPONENT, PROMOTION_PRODUCT_MD_GROUP_COMPONENT).contains(component.getName())) {
                        //                        ComponentExt.of(component).removeButtonByActionsDeeply(Lists.newArrayList(ObjectAction.CREATE.getActionCode()));
                        GroupComponent groupComponent = (GroupComponent) component;
                        List<Map> childComponents = (List) groupComponent.get(CHILD_COMPONENTS, ArrayList.class);
                        for (Map childComponent : childComponents) {
                            String refObjectApiName = (String) childComponent.get(REF_OBJECT_API_NAME);
                            if (Sets.newHashSet(PromotionProductConstants.API_NAME, PromotionRuleConstants.API_NAME).contains(refObjectApiName)) {
                                List<Map> childMap = (List) childComponent.get(CHILD_COMPONENTS);
                                for (Map tempMap : childMap) {
                                    List<Map> buttonMap = (List) tempMap.get(BUTTIONS);
                                    buttonMap.removeIf(btnMap -> btnMap.get(ACTION).toString().equals(ObjectAction.CREATE.getActionCode()));
                                }
                            }
                        }
                    }
                    //相关
                }
                layout.setComponents(components);
            }
            result.setLayout(LayoutDocument.of(layout));
        } catch (MetadataServiceException e) {
            log.warn("layout getComponent error:{}", arg, e);
        }
        return result;
    }

    private List<IFormField> getHeaderInfos() {
        List<IFormField> formFieldList = Lists.newArrayList();

        IFormField formField1 = new FormField();
        formField1.setFieldName(SystemConstants.Field.RecordType.apiName);
        formField1.setReadOnly(true);
        formField1.setRenderType(SystemConstants.RenderType.RecordType.renderType);
        formField1.setRequired(true);

        IFormField formField2 = new FormField();
        formField2.setFieldName(PromotionConstants.Field.Type.apiName);
        formField2.setReadOnly(true);
        formField2.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField2.setRequired(true);

        IFormField formField3 = new FormField();
        formField3.setFieldName(PromotionConstants.Field.Status.apiName);
        formField3.setReadOnly(true);
        formField3.setRenderType(SystemConstants.RenderType.TrueOrFalse.renderType);
        formField3.setRequired(true);

        IFormField formField4 = new FormField();
        formField4.setFieldName(PromotionConstants.Field.StartTime.apiName);
        formField4.setReadOnly(true);
        formField4.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField4.setRequired(true);

        IFormField formField5 = new FormField();
        formField5.setFieldName(PromotionConstants.Field.EndTime.apiName);
        formField5.setReadOnly(true);
        formField5.setRenderType(SystemConstants.RenderType.Date.renderType);
        formField5.setRequired(true);

        IFormField formField6 = new FormField();
        formField6.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField6.setReadOnly(true);
        formField6.setRenderType(SystemConstants.RenderType.SelectOne.renderType);
        formField6.setRequired(true);

        formFieldList.add(formField1);
        formFieldList.add(formField2);
        formFieldList.add(formField3);
        formFieldList.add(formField4);
        formFieldList.add(formField5);
        formFieldList.add(formField6);
        return formFieldList;
    }
}

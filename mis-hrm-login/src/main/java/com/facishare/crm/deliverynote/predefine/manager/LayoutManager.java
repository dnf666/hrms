package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.exception.DeliveryNoteBusinessException;
import com.facishare.crm.exception.DeliveryNoteErrorCode;
import com.facishare.crm.manager.DeliveryNoteLayoutManager;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * layout
 * Created by chenzs on 2018/1/9.
 */
@Service
@Slf4j
public class LayoutManager {
    @Autowired
    private DeliveryNoteLayoutManager deliveryNoteLayoutManager;

    /**
     * 发货单DetailLayout
     */
    public ILayout generateDeliveryNoteDetailLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();

        //基本信息
        List<IFormField> formFields = deliveryNoteLayoutManager.getDeliveryNoteFormFields(false);

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(fsUserId).tenantId(tenantId).name(DeliveryNoteObjConstants.DEFAULT_LAYOUT_API_NAME).displayName(DeliveryNoteObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(DeliveryNoteObjConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    /**
     * 发货单ListLayout
     */
    public ILayout generateDeliveryNoteListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = deliveryNoteLayoutManager.getDeliveryNoteTableColumns(false);
        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(DeliveryNoteObjConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(DeliveryNoteObjConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(DeliveryNoteObjConstants.LIST_LAYOUT_API_NAME).displayName(DeliveryNoteObjConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    /**
     * 发货单产品DetailLayout
     */
    public ILayout generateDeliveryNoteProductDetailLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();

        List<IFormField> formFields = deliveryNoteLayoutManager.getDeliveryNoteProductFormFields(false);
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(fsUserId).tenantId(tenantId).name(DeliveryNoteProductObjConstants.DEFAULT_LAYOUT_API_NAME).displayName(DeliveryNoteProductObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(DeliveryNoteProductObjConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    /**
     * 发货单产品ListLayout
     */
    public ILayout generateDeliveryNoteProductListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = deliveryNoteLayoutManager.getDeliveryNoteProductTableColumns(false);
        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(DeliveryNoteProductObjConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(DeliveryNoteProductObjConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(DeliveryNoteProductObjConstants.LIST_LAYOUT_API_NAME).displayName(DeliveryNoteProductObjConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    /**
     * 获取defaultLayout or ListLayout
     * layoutType, 只支持SystemConstants.LayoutType.Detail.layoutType， SystemConstants.LayoutType.List.layoutType
     */
    public ILayout getLayout(String tenantId, String objectApiName, String layoutType) {
        //查询layout
        List<ILayout> layouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(objectApiName, tenantId);

        for (ILayout layout : layouts) {
            if (Objects.equals(layout.getLayoutType(), layoutType)) {
                return layout;
            }
        }
        return null;
    }

    /**
     * fieldApiName改为非必填
     */
    public void changeFieldRequireToFalse(User user, String objectApiName, String fieldApiName) {
        //查找layout
        List<ILayout> layouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(objectApiName, user.getTenantId());

        //拿到defaultLayout
        ILayout defaultLayout = null;
        for (ILayout layout : layouts) {
            if (Objects.equals(layout.getLayoutType(), SystemConstants.LayoutType.Detail.layoutType)) {
                defaultLayout = layout;
            }
        }

        if (defaultLayout == null) {
            log.warn("defaultLayout:{}", defaultLayout);
            throw new com.facishare.crm.exception.DeliveryNoteBusinessException(DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, DeliveryNoteErrorCode.LAYOUT_INFO_ERROR.getMessage());
        }

        //替换
        changeFieldRequireToFalse(defaultLayout, fieldApiName);
    }

    /**
     * fieldApiName改为非必填
     */
    public void changeFieldRequireToFalse(ILayout layout, String fieldApiName) {
        //1、获取FormComponent
        List<IComponent> components = null;
        FormComponent formComponent = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        for (IComponent iComponent : components) {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                formComponent = (FormComponent) iComponent;
                break;
            }
        }

        //2、获取formFields
        IFieldSection baseFieldSection = null;
        List<IFieldSection> fieldSections = formComponent.getFieldSections();
        for (IFieldSection fieldSection : fieldSections) {
            if (Objects.equals(fieldSection.getName(), LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                baseFieldSection = fieldSection;
                break;
            }
        }
        List<IFormField> formFields = baseFieldSection.getFields();

        //3、是否有fieldApiName对应的IFormField信息
        boolean hasField = false;
        if (!CollectionUtils.isEmpty(formFields)) {
            for (IFormField oldFormField : formFields) {
                if (Objects.equals(oldFormField.getFieldName(), fieldApiName)) {
                    hasField = true;
                    oldFormField.setRequired(false);
                    break;
                }
            }
        }

        //4、更新
        if (hasField) {
            baseFieldSection.setFields(formFields);
            deliveryNoteLayoutManager.replace(layout);
        }
    }
}
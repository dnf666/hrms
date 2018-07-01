package com.facishare.crm.manager;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.exception.DeliveryNoteBusinessException;
import com.facishare.crm.exception.DeliveryNoteErrorCode;
import com.facishare.paas.metadata.api.service.ILayoutService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * layout
 * Created by chenzs on 2018/1/9.
 */
@Service
@Slf4j
public class DeliveryNoteLayoutManager {
    @Resource
    private ILayoutService layoutService;

    /**
     * 获取FormFields
     * @param hasOpenStock 是否开启了库存
     */
    public List<IFormField> getDeliveryNoteFormFields(boolean hasOpenStock) {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.Name.apiName).readOnly(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.SalesOrderId.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.DeliveryDate.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.ExpressOrg.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.ExpressOrderId.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build());

        //这里要'只读'+'必填'，但是'只读'和'必填'只能有一个，选'只读'
        formFields.add(getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName));

        if (hasOpenStock) {
            formFields.add(getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName));
        }
        //新建数据，展示"负责人（王凡：list layout用于终端，终端的新建 还是用detailLayout）
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build());
        formFields.add(getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.ReceiveDate.apiName));
        formFields.add(getDeliveryNoteFormField(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName));
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.Status.apiName).readOnly(true).renderType(SystemConstants.RenderType.SelectOne.renderType).required(false).build());

        return formFields;
    }

    /**
     * 获取'DeliveryWarehouseId'字段
     */
    public IFormField getDeliveryNoteFormField(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.ReceiveDate.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.ReceiveDate.apiName).readOnly(true).renderType(SystemConstants.RenderType.Date.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.ReceiveRemark.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build();
        }
        return null;
    }

    /**
     * 获取TableColumns （只是用在listLayout，listLayout用在手机端点击'发货单'进去的列表页）
     * @param hasOpenStock 是否开启了库存
     */
    public List<ITableColumn> getDeliveryNoteTableColumns(boolean hasOpenStock) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.Name.apiName).lableName(DeliveryNoteObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.ExpressOrg.apiName).lableName(DeliveryNoteObjConstants.Field.ExpressOrg.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.ExpressOrderId.apiName).lableName(DeliveryNoteObjConstants.Field.ExpressOrderId.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        if (hasOpenStock) {
            tableColumns.add(getDeliveryNoteTableColumn(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName));
        }
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.DeliveryDate.apiName).lableName(DeliveryNoteObjConstants.Field.DeliveryDate.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.Status.apiName).lableName(DeliveryNoteObjConstants.Field.Status.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());

        return tableColumns;
    }

    /**
     * 获取'DeliveryWarehouseId'字段
     */
    public ITableColumn getDeliveryNoteTableColumn(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName).lableName(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build();
        }
        return null;
    }

    /**
     * 获取FormFields
     * @param hasOpenStock 是否开启了库存
     * @return
     */
    public List<IFormField> getDeliveryNoteProductFormFields(boolean hasOpenStock) {
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.Name.apiName).readOnly(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName).readOnly(true).renderType(SystemConstants.RenderType.MasterDetail.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName).readOnly(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.ProductId.apiName).readOnly(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.Specs.apiName).readOnly(true).renderType(SystemConstants.RenderType.Quote.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.Unit.apiName).readOnly(true).renderType(SystemConstants.RenderType.Quote.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName).readOnly(false).renderType(SystemConstants.RenderType.Number.renderType).required(true).build());
        formFields.add(getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName));
        formFields.add(getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName));

        /**
         * Stock("stock", "产品库存"),
         * RealStock("real_stock", "实际库存")
         * 开启库存才创建
         */
        if (hasOpenStock) {
            //库存字段不显示
//          formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.StockId.apiName).readOnly(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
            formFields.add(getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.RealStock.apiName));
        }
        formFields.add(getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName));

        formFields.add(FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build());
        formFields.add(getDeliveryNoteProductFormField(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));

        return formFields;
    }

    /**
     * 获取'RealStock'字段
     */
    public IFormField getDeliveryNoteProductFormField(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.AvgPrice.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.RealStock.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.RealStock.apiName).readOnly(true).renderType(SystemConstants.RenderType.Quote.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName).readOnly(false).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName)) {
            return FormFieldBuilder.builder().fieldName(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build();
        }
        return null;
    }

    /**
     * 获取TableColumns （只是用在listLayout，listLayout用在手机端点击'发货单产品'进去的列表页，因为'发货单产品'是从对象，所以没用到）
     * @param hasOpenStock 是否开启了库存
     */
    public List<ITableColumn> getDeliveryNoteProductTableColumns(boolean hasOpenStock) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.Name.apiName).lableName(DeliveryNoteProductObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName).lableName(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName).lableName(DeliveryNoteProductObjConstants.Field.SalesOrderId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.ProductId.apiName).lableName(DeliveryNoteProductObjConstants.Field.ProductId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.Specs.apiName).lableName(DeliveryNoteProductObjConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.Unit.apiName).lableName(DeliveryNoteProductObjConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName).lableName(DeliveryNoteProductObjConstants.Field.OrderProductAmount.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName).lableName(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName).lableName(DeliveryNoteProductObjConstants.Field.DeliveryNum.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName));
        tableColumns.add(getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName));

        /**
         * Stock("stock", "产品库存"),
         * RealStock("real_stock", "实际库存")
         * 开启库存才创建
         */
        if (hasOpenStock) {
            //库存字段不显示
//          tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.StockId.apiName).lableName(DeliveryNoteProductObjConstants.Field.StockId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
            tableColumns.add(getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.RealStock.apiName));
        }

        tableColumns.add(getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName));

        tableColumns.add(TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.Remark.apiName).lableName(DeliveryNoteProductObjConstants.Field.Remark.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(getDeliveryNoteProductTableColumn(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));

        return tableColumns;
    }

    /**
     * 获取'RealStock'字段
     */
    public ITableColumn getDeliveryNoteProductTableColumn(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.AvgPrice.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName).lableName(DeliveryNoteProductObjConstants.Field.AvgPrice.label).renderType(SystemConstants.RenderType.Number.renderType).build();
        }
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName).lableName(DeliveryNoteProductObjConstants.Field.DeliveryMoney.label).renderType(SystemConstants.RenderType.Number.renderType).build();
        }
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.RealStock.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.RealStock.apiName).lableName(DeliveryNoteProductObjConstants.Field.RealStock.label).renderType(SystemConstants.RenderType.Quote.renderType).build();
        }
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName).lableName(DeliveryNoteProductObjConstants.Field.RealReceiveNum.label).renderType(SystemConstants.RenderType.Number.renderType).build();
        }
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName)) {
            return TableColumnBuilder.builder().name(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName).lableName(DeliveryNoteProductObjConstants.Field.ReceiveRemark.label).renderType(SystemConstants.RenderType.Text.renderType).build();
        }
        return null;
    }

    /**
     * 如果没addFieldApiNames对应的信息，在afterFieldApiName后面加上addFormField，如果没有afterFieldApiName, 则加在最后
     */
    public void detailLayoutAddField(ILayout layout, List<String> addFieldApiNames, Map<String, IFormField> addFieldApiName2FormFieldMap, Map<String, String> addFieldApiName2afterFieldApiNameMap) {
        if (CollectionUtils.isEmpty(addFieldApiNames)) {
            return;
        }
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

        //3、是否有addFieldApiNames对应的IFormField信息，没有则添加
        boolean isNeedReplace = false;
        for (String addFieldApiName : addFieldApiNames) {
            //是否有要加的字段
            boolean hasField = false;
            for (IFormField oldFormField : formFields) {
                if (Objects.equals(oldFormField.getFieldName(), addFieldApiName)) {
                    hasField = true;
                    break;
                }
            }

            //没有则添加字段
            if (!hasField) {
                isNeedReplace = true;
                IFormField addFormField = addFieldApiName2FormFieldMap.get(addFieldApiName);
                String afterFieldApiName = addFieldApiName2afterFieldApiNameMap.get(addFieldApiName);
                formFields = getNewFields(formFields, addFormField, afterFieldApiName);
            }
        }

        //4、替换
        if (isNeedReplace) {
            baseFieldSection.setFields(formFields);
            replace(layout);
        }
    }

    /**
     * 在afterFieldApiName后面加上addFormField，没有afterFieldApiName，则加在最后
     */
    private List<IFormField> getNewFields(List<IFormField> oldFormFields, IFormField addFormField, String afterFieldApiName) {
        List<IFormField> newFormFields = new ArrayList<>();

        boolean hasNeedAddField = false;
        for (IFormField oldFormField : oldFormFields) {
            newFormFields.add(oldFormField);
            if (Objects.equals(oldFormField.getFieldName(), afterFieldApiName)) {
                newFormFields.add(addFormField);
                hasNeedAddField = true;
            }
        }

        if (!hasNeedAddField) {
            newFormFields.add(addFormField);
        }

        return newFormFields;
    }

    /**
     * 如果没addFieldApiNames对应的信息，在afterFieldApiName后面加上addTableColumn，如果没有afterFieldApiName, 则加在最后
     */
    public void listLayoutAddField(ILayout layout, List<String> addFieldApiNames, Map<String, ITableColumn> addFieldApiName2TableColumnMap, Map<String, String> addFieldApiName2afterFieldApiNameMap) {
        if (CollectionUtils.isEmpty(addFieldApiNames)) {
            return;
        }

        //1、获取tableColumns
        List<IComponent> components = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        TableComponent tableComponent = (TableComponent) components.get(0);
        List<ITableColumn> tableColumns = tableComponent.getIncludeFields();

        //3、是否有addFieldApiNames对应的IFormField信息，没有则添加
        boolean isNeedReplace = false;
        for (String addFieldApiName : addFieldApiNames) {
            //是否有要加的字段
            boolean hasField = false;
            for (ITableColumn oldTableColumn : tableColumns) {
                if (Objects.equals(oldTableColumn.getName(), addFieldApiName)) {
                    hasField = true;
                    break;
                }
            }

            //没有则添加字段
            if (!hasField) {
                isNeedReplace = true;
                ITableColumn addTableColumn = addFieldApiName2TableColumnMap.get(addFieldApiName);
                String afterFieldApiName = addFieldApiName2afterFieldApiNameMap.get(addFieldApiName);
                tableColumns = getNewTableColumns(tableColumns, addTableColumn, afterFieldApiName);
            }
        }

        //4、替换
        if (isNeedReplace) {
            tableComponent.setIncludeFields(tableColumns);
            replace(layout);
        }
    }

    /**
     * 如果没addTableColumn.getName()对应的信息，在afterFieldApiName后面加上addFormField，如果没有afterFieldApiName, 则加在最后
     */
    private List<ITableColumn> getNewTableColumns(List<ITableColumn> oldTableColumns, ITableColumn addTableColumn, String afterFieldApiName) {
        List<ITableColumn> newTableColumns = new ArrayList<>();

        boolean hasNeedAddField = false;
        for (ITableColumn oldTableColumn : oldTableColumns) {
            newTableColumns.add(oldTableColumn);
            if (Objects.equals(oldTableColumn.getName(), afterFieldApiName)) {
                newTableColumns.add(addTableColumn);
                hasNeedAddField = true;
            }
        }

        if (!hasNeedAddField) {
            newTableColumns.add(addTableColumn);
        }

        return newTableColumns;
    }

    public void replace(ILayout layout) {
        try {
            ILayout result = layoutService.replace(layout);
            log.info("layoutService.replace success, layout:{}, result:{}", layout, result);
        } catch (MetadataServiceException e) {
            log.warn("layoutService.replace failed, layout:{}", layout);
            throw new DeliveryNoteBusinessException(() -> e.getErrorCode().getCode(), "更新layout信息失败，" + e.getMessage());
        } catch (Exception e) {
            log.warn("layoutService.replace failed, layout:{}", layout);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.REPLACE_LAYOUT_FAILED, DeliveryNoteErrorCode.REPLACE_LAYOUT_FAILED.getMessage() + e.getMessage());
        }
    }

    public List<ILayout> findByObjectDescribeApiNameAndTenantId(String objectDescribeApiName, String tenantId) {
        List<ILayout> layouts;
        try {
            layouts = layoutService.findByObjectDescribeApiNameAndTenantId(objectDescribeApiName, tenantId);
        } catch (MetadataServiceException e) {
            log.warn("layoutService.findByObjectDescribeApiNameAndTenantId failed, objectDescribeApiName:{}, tenantId:{}", objectDescribeApiName, tenantId, e);
            throw new DeliveryNoteBusinessException(() -> e.getErrorCode().getCode(), "查询layout信息失败，" + e.getMessage());
        }
        return layouts;
    }
}
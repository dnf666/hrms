package com.facishare.crm.customeraccount.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.describebuilder.FieldSectionBuilder;
import com.facishare.crm.describebuilder.FormComponentBuilder;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.LayoutBuilder;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.TableColumn;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitUtil {

    public static ILayout generatePrepayDetailDefaultLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.IncomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OutcomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OnlineChargeNo.apiName).renderType(SystemConstants.RenderType.Text.renderType).required(false).readOnly(false).build());
        //TODO orderPayment
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OrderPayment.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Refund.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Remark.apiName).renderType(SystemConstants.RenderType.LongText.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Attach.apiName).renderType(SystemConstants.RenderType.FileAttachment.renderType).required(false).readOnly(false).build());

        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).fields(formFields).showHeader(true).build();
        FieldSection systemFieldSection = getSystemFieldSection();
        fieldSections.add(baseFieldSection);
        fieldSections.add(systemFieldSection);

        components.add(FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build());
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).name(PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME).displayName(PrepayDetailConstants.DEFUALT_LAYOUT_DISPLAY_NAME).refObjectApiName(PrepayDetailConstants.API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).components(components).build();
    }

    public static FieldSection getSystemFieldSection() {
        List<IFormField> systemFormFields = Lists.newArrayList();
        systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.CreateBy.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(false).readOnly(true).build());
        systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.CreateTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(false).readOnly(true).build());
        systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LastModifiedBy.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(false).readOnly(true).build());
        systemFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LastModifiedTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(false).readOnly(true).build());
        FieldSection systemFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.SYSTEM_FIELD_SECTION_API_NAME).header(LayoutConstants.SYSTEM_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(systemFormFields).build();
        return systemFieldSection;
    }

    public static ILayout generateCustomerAccountDetailLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.MasterDetail.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.PrepayBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.PrepayLockedBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.RebateBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.RebateAvailableBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.RebateLockedBalance.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.CreditQuota.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(CustomerAccountConstants.Field.SettleType.apiName).renderType(SystemConstants.RenderType.SelectMany.renderType).required(true).readOnly(false).build());

        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(baseFieldSection);

        FieldSection systemFieldSection = getSystemFieldSection();
        fieldSections.add(systemFieldSection);

        components.add(FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build());
        return LayoutBuilder.builder().name(CustomerAccountConstants.DETAIL_LAYOUT_API_NAME).displayName(CustomerAccountConstants.DETAIL_LAYOUT_DISPLAY_NAME).refObjectApiName(CustomerAccountConstants.API_NAME).tenantId(tenantId).createBy(fsUserId).layoutType(SystemConstants.LayoutType.Detail.layoutType).isDefault(true).components(components).build();
    }

    public static ILayout generatePrepayIncomeLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.IncomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OnlineChargeNo.apiName).renderType(SystemConstants.RenderType.Text.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Refund.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Remark.apiName).renderType(SystemConstants.RenderType.LongText.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Attach.apiName).renderType(SystemConstants.RenderType.FileAttachment.renderType).required(false).readOnly(false).build());

        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).fields(formFields).showHeader(true).build();
        FieldSection systemFieldSection = getSystemFieldSection();

        List<IFieldSection> fieldSections = Lists.newArrayList();
        fieldSections.add(baseFieldSection);
        fieldSections.add(systemFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build();
        components.add(formComponent);
        return LayoutBuilder.builder().name(PrepayDetailConstants.INCOME_LAYOUT_API_NAME).displayName(PrepayDetailConstants.INCOME_LAYOUT_DISPLAY_NAME).refObjectApiName(PrepayDetailConstants.API_NAME).layoutType(SystemConstants.LayoutType.Detail.layoutType).isDefault(false).tenantId(tenantId).createBy(fsUserId).components(components).build();
    }

    public static ILayout generatePrepayOutcomeLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OutcomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        //TODO
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OrderPayment.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.Remark.apiName).renderType(SystemConstants.RenderType.LongText.renderType).required(false).readOnly(false).build());
        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        FieldSection systemFieldSection = getSystemFieldSection();

        List<IFieldSection> fieldSections = Lists.newArrayList();
        fieldSections.add(baseFieldSection);
        fieldSections.add(systemFieldSection);
        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build();
        components.add(formComponent);
        return LayoutBuilder.builder().name(PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME).displayName(PrepayDetailConstants.OUTCOME_LAYOUT_DISPLAY_NAME).layoutType(SystemConstants.LayoutType.Detail.layoutType).isDefault(false).tenantId(tenantId).createBy(fsUserId).refObjectApiName(PrepayDetailConstants.API_NAME).components(components).build();
    }

    /**
     *
     * 备注：所有老数据迁移完成该函数可以删除<br>
     * @param user
     * @param layout
     * @return
     */
    public static ILayout updatePrepayDetailLayoutForOrderPaymentReplace(User user, ILayout layout) {
        try {
            for (IComponent component : layout.getComponents()) {
                if (component instanceof FormComponent) {
                    FormComponent formComponent = (FormComponent) component;
                    for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                        fieldSection.getFields().forEach(formFiled -> {
                            if (formFiled.getFieldName().equals(PrepayDetailConstants.Field.Payment.apiName)) {
                                formFiled.setFieldName(PrepayDetailConstants.Field.OrderPayment.apiName);
                            }
                        });
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.warn("updatePrepayDetailLayoutForOrderPaymentReplace error occur,for exception:{}", e);
        }
        return layout;
    }

    public static ILayout updateRebateOutcomeLayoutForOrderPaymentReplace(User user, ILayout layout) {
        try {
            for (IComponent component : layout.getComponents()) {
                if (component instanceof FormComponent) {
                    FormComponent formComponent = (FormComponent) component;
                    for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                        fieldSection.getFields().forEach(formFiled -> {
                            if (formFiled.getFieldName().equals(RebateOutcomeDetailConstants.Field.Payment.apiName)) {
                                formFiled.setFieldName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName);
                            }
                        });
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.warn("updateRebateOutcomeLayoutForOrderPaymentReplace error occur,for exception:{}", e);
        }
        return layout;
    }

    public static ILayout generateRebateIncomeDetailDefaultLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.IncomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.UsedRebate.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.StartTime.apiName).renderType(SystemConstants.RenderType.Date.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.EndTime.apiName).renderType(SystemConstants.RenderType.Date.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
        //6.3 订单返利
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.SalesOrder.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Refund.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Remark.apiName).renderType(SystemConstants.RenderType.LongText.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Attach.apiName).renderType(SystemConstants.RenderType.FileAttachment.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());
        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        FieldSection systemFieldSection = getSystemFieldSection();
        List<IFieldSection> fieldSections = Lists.newArrayList();
        fieldSections.add(baseFieldSection);
        fieldSections.add(systemFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build();
        components.add(formComponent);

        return LayoutBuilder.builder().name(RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME).displayName(RebateIncomeDetailConstants.DEFUALT_LAYOUT_DISPLAY_NAME).refObjectApiName(RebateIncomeDetailConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).isDefault(true).tenantId(tenantId).createBy(fsUserId).build();
    }

    public static ILayout generateRebateOutcomeDetailDefaultLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName).renderType(SystemConstants.RenderType.MasterDetail.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        //6.3
        formFields.add(FormFieldBuilder.builder().fieldName(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());

        FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        FieldSection systemFieldSection = getSystemFieldSection();
        List<IFieldSection> fieldSections = Lists.newArrayList(baseFieldSection, systemFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build();
        components.add(formComponent);

        return LayoutBuilder.builder().name(RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME).displayName(RebateOutcomeDetailConstants.DEFUALT_LAYOUT_DISPLAY_NAME).refObjectApiName(RebateOutcomeDetailConstants.API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).components(components).tenantId(tenantId).createBy(fsUserId).build();
    }

    public static ILayout generateRebateOutcomeDetailListLayout(String tenantId, String fsUserId, String refObjectApiName) {
        ILayout layout = new Layout();
        layout.setName(RebateOutcomeDetailConstants.LIST_LAYOUT_API_NAME);
        layout.setRefObjectApiName(refObjectApiName);
        layout.setDisplayName(RebateOutcomeDetailConstants.LIST_LAYOUT_DISPLAY_NAME);
        layout.setDeleted(false);
        layout.setTenantId(tenantId);
        layout.setCreatedBy(fsUserId);
        layout.setLastModifiedBy(fsUserId);
        layout.setLayoutType("list");
        layout.setIsDefault(false);
        layout.setPackage("CRM");
        layout.setIsShowFieldname(true);
        layout.setAgentType("agent_type_mobile");

        List<TableComponent> tableComponents = Lists.newArrayList();
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(getTableColumn(RebateOutcomeDetailConstants.Field.Name.label, RebateOutcomeDetailConstants.Field.Name.apiName, "auto_number"));
        tableColumns.add(getTableColumn(SystemConstants.Field.LifeStatus.label, SystemConstants.Field.LifeStatus.apiName, "select_one"));
        tableColumns.add(getTableColumn(RebateOutcomeDetailConstants.Field.Amount.label, RebateOutcomeDetailConstants.Field.Amount.apiName, "currency"));
        tableColumns.add(getTableColumn(RebateOutcomeDetailConstants.Field.TransactionTime.label, RebateOutcomeDetailConstants.Field.TransactionTime.apiName, "date_time"));
        tableColumns.add(getTableColumn(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.label, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, "master_detail"));
        tableColumns.add(getTableColumn(RebateOutcomeDetailConstants.Field.OrderPayment.label, RebateOutcomeDetailConstants.Field.OrderPayment.apiName, "object_reference"));
        tableColumns.add(getTableColumn(SystemConstants.Field.Owner.label, SystemConstants.Field.Owner.apiName, "employee"));
        tableColumns.add(getTableColumn(SystemConstants.Field.RecordType.label, SystemConstants.Field.RecordType.apiName, "record_type"));

        TableComponent tableComponent = getTableComponent(RebateOutcomeDetailConstants.API_NAME, tableColumns, null);
        tableComponents.add(tableComponent);

        List<IComponent> components = Lists.newArrayList();
        components.add(tableComponent);

        layout.setComponents(components);
        return layout;
    }

    public static ILayout generateCustomerAccountListLayout(String tenantId, String fsUserId, String refObjectApiName) {
        ILayout layout = new Layout();
        layout.setName(CustomerAccountConstants.LIST_LAYOUT_API_NAME);
        layout.setRefObjectApiName(refObjectApiName);
        layout.setDisplayName(CustomerAccountConstants.LIST_LAYOUT_DISPLAY_NAME);
        layout.setDeleted(false);
        layout.setTenantId(tenantId);
        layout.setCreatedBy(fsUserId);
        layout.setLastModifiedBy(fsUserId);
        layout.setLayoutType("list");
        layout.setIsDefault(false);
        layout.setPackage("CRM");
        layout.setAgentType("agent_type_mobile");
        layout.setIsShowFieldname(true);

        List<TableComponent> tableComponents = Lists.newArrayList();
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.Name.label, CustomerAccountConstants.Field.Name.apiName, "auto_number"));
        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.Customer.label, CustomerAccountConstants.Field.Customer.apiName, "master_detail"));
        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.PrepayBalance.label, CustomerAccountConstants.Field.PrepayBalance.apiName, "currency"));
        //        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.PrepayAvailableBalance.label, CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, "currency"));
        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.RebateBalance.label, CustomerAccountConstants.Field.RebateBalance.apiName, "currency"));
        //        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.RebateAvailableBalance.label, CustomerAccountConstants.Field.RebateAvailableBalance.apiName, "currency"));
        //        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.SettleType.label, CustomerAccountConstants.Field.SettleType.apiName, "select_many"));
        //        tableColumns.add(getTableColumn(CustomerAccountConstants.Field.CreditQuota.label, CustomerAccountConstants.Field.CreditQuota.apiName, "number"));
        //        tableColumns.add(getTableColumn(SystemConstants.Field.Owner.label, SystemConstants.Field.Owner.apiName, "employee"));
        //        tableColumns.add(getTableColumn(SystemConstants.Field.RecordType.label, SystemConstants.Field.RecordType.apiName, "record_type"));

        TableComponent tableComponent = getTableComponent(CustomerAccountConstants.API_NAME, tableColumns, null);
        tableComponents.add(tableComponent);

        List<IComponent> components = Lists.newArrayList();
        components.add(tableComponent);

        layout.setComponents(components);
        return layout;
    }

    private static TableColumn getTableColumn(String lableName, String name, String renderType) {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setLabelName(lableName);
        tableColumn.setName(name);
        tableColumn.setRenderType(renderType);
        return tableColumn;
    }

    private static TableComponent getTableComponent(String refObjectApiName, List<ITableColumn> tableColumns, List<IButton> buttons) {
        TableComponent tableComponent = new TableComponent();
        tableComponent.setRefObjectApiName(refObjectApiName);
        tableComponent.setIncludeFields(tableColumns);
        if (CollectionUtils.isNotEmpty(buttons)) {
            tableComponent.setButtons(buttons);
        }
        return tableComponent;
    }

    public static ILayout generateRebateIncomeDetailListLayout(String tenantId, String fsUserId, String refObjectApiName) {
        ILayout layout = new Layout();
        layout.setName(RebateIncomeDetailConstants.LIST_LAYOUT_API_NAME);
        layout.setRefObjectApiName(refObjectApiName);
        layout.setDisplayName(RebateIncomeDetailConstants.LIST_LAYOUT_DISPLAY_NAME);
        layout.setDeleted(false);
        layout.setTenantId(tenantId);
        layout.setCreatedBy(fsUserId);
        layout.setLastModifiedBy(fsUserId);
        layout.setLayoutType("list");
        layout.setIsDefault(false);
        layout.setPackage("CRM");
        layout.setAgentType("agent_type_mobile");
        layout.setIsShowFieldname(true);

        List<TableComponent> tableComponents = Lists.newArrayList();
        List<ITableColumn> tableColumns = Lists.newArrayList();
        //tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.Name.label, RebateIncomeDetailConstants.Field.Name.apiName, "auto_number"));
        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.Customer.label, RebateIncomeDetailConstants.Field.Customer.apiName, "object_reference"));
        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.Amount.label, RebateIncomeDetailConstants.Field.Amount.apiName, "currency"));
        //tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.IncomeType.label, RebateIncomeDetailConstants.Field.IncomeType.apiName, "select_one"));
        tableColumns.add(getTableColumn(SystemConstants.Field.LifeStatus.label, SystemConstants.Field.LifeStatus.apiName, "select_one"));
        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.TransactionTime.label, RebateIncomeDetailConstants.Field.TransactionTime.apiName, "date_time"));
        //        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.Refund.label, RebateIncomeDetailConstants.Field.Refund.apiName, "object_reference"));
        //        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.StartTime.label, RebateIncomeDetailConstants.Field.StartTime.apiName, "select_one"));
        //        tableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.EndTime.label, RebateIncomeDetailConstants.Field.EndTime.apiName, "date"));
        //        tableColumns.add(getTableColumn(SystemConstants.Field.Owner.label, SystemConstants.Field.Owner.apiName, "employee"));
        //        tableColumns.add(getTableColumn(SystemConstants.Field.RecordType.label, SystemConstants.Field.RecordType.apiName, "record_type"));

        TableComponent tableComponent = getTableComponent(RebateIncomeDetailConstants.API_NAME, tableColumns, null);
        tableComponents.add(tableComponent);

        List<IComponent> components = Lists.newArrayList();
        components.add(tableComponent);

        layout.setComponents(components);
        return layout;
    }

}

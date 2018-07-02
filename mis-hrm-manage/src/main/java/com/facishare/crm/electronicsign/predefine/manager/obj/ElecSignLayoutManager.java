package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.crm.electronicsign.enums.type.AccountSignCertifyLayoutTypeEnum;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 对象layout
 */
@Slf4j
@Service
public class ElecSignLayoutManager {
    /**
     * "客户签章认证"DetailLayout
     */
    public ILayout generateAccountSignCertifyLayout(String tenantId, String userId, AccountSignCertifyLayoutTypeEnum typeEnum, String name, String displayName) {
        List<IFieldSection> fieldSections = Lists.newArrayList();

        //基本信息
        List<IFormField> formFields =  getAccountSignCertifyFormFields(typeEnum);
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        //系统信息
        List<IFormField> systemInfoFormFields = Lists.newArrayList();
        systemInfoFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());
        FieldSection systemInfoFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.SYSTEM_FIELD_SECTION_API_NAME).header(LayoutConstants.SYSTEM_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(systemInfoFormFields).build();

        fieldSections.add(fieldSection);
        fieldSections.add(systemInfoFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(name).displayName(displayName).isDefault(true).refObjectApiName(AccountSignCertifyObjConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    /**
     * 获取'客户签章认证'FormFields
     */
    private List<IFormField> getAccountSignCertifyFormFields(AccountSignCertifyLayoutTypeEnum typeEnum) {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.AccountId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(false).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.RegMobile.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(false).build());

        //default 和 企业才有
        if (Objects.equals(typeEnum, AccountSignCertifyLayoutTypeEnum.DEFAULT) || Objects.equals(typeEnum, AccountSignCertifyLayoutTypeEnum.ENTERPRISE)) {
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.LegalPersonName.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.LegalPersonIdentity.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        }

        //default 和 个人才有
        if (Objects.equals(typeEnum, AccountSignCertifyLayoutTypeEnum.DEFAULT) || Objects.equals(typeEnum, AccountSignCertifyLayoutTypeEnum.INDIVIDUAL)) {
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.UserName.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.UserMobile.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
            formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.UserIdentity.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        }

        formFields.add(FormFieldBuilder.builder().fieldName(AccountSignCertifyObjConstants.Field.UseStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).readOnly(true).required(false).build());

        return formFields;
    }

    /**
     * "客户签章认证"ListLayout
     */
    public ILayout generateAccountSignCertifyListLayout(String tenantId, String userId) {
        List<ITableColumn> tableColumns = getAccountSignCertifyTableColumns();
        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(AccountSignCertifyObjConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).refObjectApiName(AccountSignCertifyObjConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(AccountSignCertifyObjConstants.LIST_LAYOUT_API_NAME).displayName(AccountSignCertifyObjConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    private List<ITableColumn> getAccountSignCertifyTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(AccountSignCertifyObjConstants.Field.Name.apiName).lableName(AccountSignCertifyObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AccountSignCertifyObjConstants.Field.AccountId.apiName).lableName(AccountSignCertifyObjConstants.Field.AccountId.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName).lableName(AccountSignCertifyObjConstants.Field.CertifyStatus.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        return tableColumns;
    }

    /**
     *  '内部签章认证'DetailLayout
     */
    public ILayout generateInternalSignCertifyDetailLayout(String tenantId, String userId, String name, String displayName, String refObjectApiName) {
        List<IFieldSection> fieldSections = Lists.newArrayList();

        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.RegMobile.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        //认证资料
        List<IFormField> certifyInfoFormFields = Lists.newArrayList();
        certifyInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        certifyInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        certifyInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.LegalPersonName.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        certifyInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        certifyInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.LegalPersonIdentity.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(false).required(true).build());
        FieldSection certifyInfoFieldSection = FieldSectionBuilder.builder().name(InternalSignCertifyObjConstants.CERTIFY_FIELD_SECTION_API_NAME).header(InternalSignCertifyObjConstants.CERTIFY_FIELD_SECTION).showHeader(true).fields(certifyInfoFormFields).build();

        //系统信息
        List<IFormField> systemInfoFormFields = Lists.newArrayList();
        systemInfoFormFields.add(FormFieldBuilder.builder().fieldName(InternalSignCertifyObjConstants.Field.UseStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).readOnly(true).required(false).build());
        systemInfoFormFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).readOnly(false).required(true).build());
        FieldSection systemInfoFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.SYSTEM_FIELD_SECTION_API_NAME).header(LayoutConstants.SYSTEM_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(systemInfoFormFields).build();

        fieldSections.add(fieldSection);
        fieldSections.add(certifyInfoFieldSection);
        fieldSections.add(systemInfoFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(name).displayName(displayName).isDefault(true).refObjectApiName(refObjectApiName).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    /**
     *  DetailLayout
     */
    public ILayout generateDetailLayout(String tenantId, String userId, String name, String displayName, String refObjectApiName) {
        List<IFieldSection> fieldSections = Lists.newArrayList();

        //基本信息
        List<IFormField> formFields = null;
        //签署记录
        if (Objects.equals(refObjectApiName, SignRecordObjConstants.API_NAME)) {
            formFields = getSignRecordFormFields(tenantId);
        } else if (Objects.equals(refObjectApiName, SignerObjConstants.API_NAME)) {
            formFields = getSignerFormFields();
        } else {
            return null;
        }

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(name).displayName(displayName).isDefault(true).refObjectApiName(refObjectApiName).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    /**
     * 获取'签署记录'FormFields
     */
    private List<IFormField> getSignRecordFormFields(String tenantId) {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).readOnly(true).required(false).build());

        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.QuotaType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.AppType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.Origin.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).readOnly(true).required(false).build());

        // TODO: 2018/5/22 chenzs 加字段要调整
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.SalesOrderId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(true).required(false).build());

        boolean isUseCustomAccountStatementObjApiName = ConfigCenter.isUseCustomAccountStatementObjApiName(tenantId);
        log.info("generateSignRecordDescribeDraft, tenantId[{}], isUseCustomAccountStatementObjApiName[{}]", tenantId, isUseCustomAccountStatementObjApiName);
        if (isUseCustomAccountStatementObjApiName) {
            formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.AccountStatementId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(true).required(false).build());
        }
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.DeliveryNoteId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(true).required(false).build());

        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.ContractId.apiName).renderType(SystemConstants.RenderType.Text.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignRecordObjConstants.Field.ContractFileAttachment.apiName).renderType("file_attachment").readOnly(true).required(false).build());

        return formFields;
    }

    /**
     * 获取'签署记录'FormFields
     */
    private List<IFormField> getSignerFormFields() {
        //基本信息
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(SignerObjConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignerObjConstants.Field.SignRecordId.apiName).renderType(SystemConstants.RenderType.MasterDetail.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignerObjConstants.Field.AccountSignCertifyId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(true).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SignerObjConstants.Field.InternalSignCertifyId.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).readOnly(true).required(false).build());

        return formFields;
    }

    /**
     * ListLayout
     */
    public ILayout generateListLayout(String tenantId, String fsUserId, String name, String displayName, String refObjectApiName) {
        List<ITableColumn> tableColumns = null;
        //"签署记录"
        if (Objects.equals(refObjectApiName, InternalSignCertifyObjConstants.API_NAME)) {
            tableColumns = getInternalSignCertifyTableColumns();
        } else if (Objects.equals(refObjectApiName, SignRecordObjConstants.API_NAME)) {
            tableColumns = getSignRecordTableColumns();
        } else if (Objects.equals(refObjectApiName, SignerObjConstants.API_NAME)) {
            tableColumns = getSignerTableColumns();
        } else {
            return null;
        }
        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(refObjectApiName).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(refObjectApiName).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(name).displayName(displayName).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    /**
     * '内部签章认证'
     */
    private List<ITableColumn> getInternalSignCertifyTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(InternalSignCertifyObjConstants.Field.Name.apiName).lableName(InternalSignCertifyObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(InternalSignCertifyObjConstants.Field.RegMobile.apiName).lableName(InternalSignCertifyObjConstants.Field.RegMobile.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName).lableName(InternalSignCertifyObjConstants.Field.EnterpriseName.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.CreateTime.apiName).lableName(SystemConstants.Field.CreateTime.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        return tableColumns;
    }

    /**
     * '签署记录'
     */
    private List<ITableColumn> getSignRecordTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(SignRecordObjConstants.Field.Name.apiName).lableName(SignRecordObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SignRecordObjConstants.Field.QuotaType.apiName).lableName(SignRecordObjConstants.Field.QuotaType.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SignRecordObjConstants.Field.Origin.apiName).lableName(SignRecordObjConstants.Field.Origin.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.CreateTime.apiName).lableName(SystemConstants.Field.CreateTime.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        return tableColumns;
    }

    /**
     * '签署方'
     */
    private List<ITableColumn> getSignerTableColumns() {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(SignerObjConstants.Field.Name.apiName).lableName(SignerObjConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SignerObjConstants.Field.SignRecordId.apiName).lableName(SignerObjConstants.Field.SignRecordId.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SignerObjConstants.Field.AccountSignCertifyId.apiName).lableName(SignerObjConstants.Field.AccountSignCertifyId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SignerObjConstants.Field.InternalSignCertifyId.apiName).lableName(SignerObjConstants.Field.InternalSignCertifyId.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        return tableColumns;
    }
}
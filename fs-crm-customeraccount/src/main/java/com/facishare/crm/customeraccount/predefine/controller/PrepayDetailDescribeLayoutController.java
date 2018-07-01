package com.facishare.crm.customeraccount.predefine.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.PrepayOutcomeTypeEnum;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Sets;

import io.netty.util.internal.StringUtil;

public class PrepayDetailDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result after(Arg arg, Result result) {
        IObjectDescribe objectDescribe = result.getObjectDescribe().toObjectDescribe();

        String layoutType = arg.getLayout_type();
        String recordTypeApiName = arg.getRecordType_apiName();
        Layout layout = new Layout(result.getLayout());
        List<IComponent> components = null;
        components = getComponents(layout, components);
        filterLayout(result, layoutType, recordTypeApiName, components);
        filterSelectOneType(objectDescribe, layoutType, recordTypeApiName);
        return result;
    }

    private void filterSelectOneType(IObjectDescribe objectDescribe, String layoutType, String recordTypeApiName) {
        if (PrepayDetailConstants.RecordType.IncomeRecordType.apiName.equals(recordTypeApiName)) {
            if (UdobjConstants.LAYOUT_TYPE_ADD.equals(layoutType)) {//|| UdobjConstants.LAYOUT_TYPE_EDIT.equals(layoutType)
                SelectOneFieldDescribe fieldDescribe = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe("income_type");
                List<ISelectOption> selectOptions = fieldDescribe.getSelectOptions();
                Iterator iterator = selectOptions.iterator();
                while (iterator.hasNext()) {
                    ISelectOption option = (ISelectOption) iterator.next();
                    if (PrepayIncomeTypeEnum.OrderRefund.getLabel().equals(option.getLabel()) || PrepayIncomeTypeEnum.OnlineCharge.getLabel().equals(option.getLabel())) {
                        iterator.remove();
                    }
                }
                fieldDescribe.setSelectOptions(selectOptions);
            }
        } else if (PrepayDetailConstants.RecordType.OutcomeRecordType.apiName.equals(recordTypeApiName)) {
            if (UdobjConstants.LAYOUT_TYPE_ADD.equals(layoutType)) {//|| UdobjConstants.LAYOUT_TYPE_EDIT.equals(layoutType)
                SelectOneFieldDescribe fieldDescribe = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe("outcome_type");
                List<ISelectOption> selectOptions = fieldDescribe.getSelectOptions();
                Iterator iterator = selectOptions.iterator();
                while (iterator.hasNext()) {
                    ISelectOption option = (ISelectOption) iterator.next();
                    if (PrepayOutcomeTypeEnum.OffsetOrder.getLabel().equals(option.getLabel())) {
                        iterator.remove();
                    }
                }
                fieldDescribe.setSelectOptions(selectOptions);
            }
        }
    }

    private void filterLayout(Result result, String layoutType, String recordTypeApiName, List<IComponent> components) {
        //过滤客户账户layout字段
        for (IComponent component : components) {
            List<IFieldSection> fieldSections = ((FormComponent) component).getFieldSections();
            for (IFieldSection fieldSection : fieldSections) {
                if (fieldSection.getName().equals("base_field_section__c")) {
                    List<IFormField> formFields = fieldSection.getFields();
                    formFields.removeIf(formField -> formField.getFieldName().equals(PrepayDetailConstants.Field.CustomerAccount.apiName));
                    fieldSection.setFields(formFields);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(layoutType)) {
            if (UdobjConstants.LAYOUT_TYPE_ADD.equals(layoutType)) {
                if (StringUtils.isNotBlank(recordTypeApiName) && PrepayDetailConstants.RecordType.IncomeRecordType.apiName.equals(recordTypeApiName)) {
                    for (IComponent component : components) {
                        FormComponent fc = (FormComponent) component;
                        List<IFieldSection> fields = fc.getFieldSections();
                        for (IFieldSection field : fields) {
                            List<IFormField> formFields = field.getFields();
                            Iterator iterator = formFields.iterator();
                            while (iterator.hasNext()) {
                                IFormField formField = (IFormField) iterator.next();
                                if (PrepayDetailConstants.Field.Refund.apiName.equals(formField.getFieldName()) || PrepayDetailConstants.Field.OnlineChargeNo.apiName.equals(formField.getFieldName()) || SystemConstants.Field.LifeStatus.apiName.equals(formField.getFieldName())) {
                                    iterator.remove();
                                }
                            }
                            field.setFields(formFields);
                        }
                    }
                } else if (StringUtils.isNotBlank(recordTypeApiName) && PrepayDetailConstants.RecordType.OutcomeRecordType.apiName.equals(recordTypeApiName)) {
                    for (IComponent component : components) {
                        FormComponent fc = (FormComponent) component;
                        List<IFieldSection> fields = fc.getFieldSections();
                        for (IFieldSection field : fields) {
                            List<IFormField> formFields = field.getFields();
                            Iterator iterator = formFields.iterator();
                            while (iterator.hasNext()) {
                                IFormField formField = (IFormField) iterator.next();
                                if (PrepayDetailConstants.Field.OrderPayment.apiName.equals(formField.getFieldName()) || SystemConstants.Field.LifeStatus.apiName.equals(formField.getFieldName())) {
                                    iterator.remove();
                                }
                            }
                            field.setFields(formFields);
                        }
                    }
                }
            } else if (UdobjConstants.LAYOUT_TYPE_EDIT.equals(layoutType)) {
                IObjectData objectData = result.getObjectData().toObjectData();
                String lifeStatus = objectData.get("life_status", String.class);
                if (!SystemConstants.LifeStatus.Ineffective.equals(lifeStatus)) {
                    for (IComponent component : components) {
                        FormComponent fc = (FormComponent) component;
                        List<IFieldSection> fields = fc.getFieldSections();
                        for (IFieldSection field : fields) {
                            List<IFormField> formFields = field.getFields();
                            Iterator iterator = formFields.iterator();
                            while (iterator.hasNext()) {
                                IFormField formField = (IFormField) iterator.next();
                                String fieldName = formField.getFieldName();
                                Set<String> notEditFields = Sets.newHashSet(PrepayDetailConstants.Field.Amount.apiName, PrepayDetailConstants.Field.Customer.apiName, PrepayDetailConstants.Field.IncomeType.apiName, PrepayDetailConstants.Field.OutcomeType.apiName, PrepayDetailConstants.Field.OrderPayment.apiName);
                                if (notEditFields.contains(fieldName)) {
                                    formField.setReadOnly(true);
                                }
                            }
                            field.setFields(formFields);
                        }
                    }
                }

            }
        }
    }

    private List<IComponent> getComponents(Layout layout, List<IComponent> components) {
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.error("prepayDetailDescribeLayoutController error", e);
        }
        return components;
    }

}

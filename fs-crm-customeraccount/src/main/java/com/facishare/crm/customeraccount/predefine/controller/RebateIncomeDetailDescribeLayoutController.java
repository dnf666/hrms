package com.facishare.crm.customeraccount.predefine.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
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

public class RebateIncomeDetailDescribeLayoutController extends StandardDescribeLayoutController {
    @Override
    protected Result after(Arg arg, Result result) {
        IObjectDescribe objectDescribe = result.getObjectDescribe().toObjectDescribe();
        String layoutType = arg.getLayout_type();
        Layout layout = new Layout(result.getLayout());
        List<IComponent> components = null;
        components = getComponents(layout, components);
        filterLayout(result, layoutType, components);
        filterIncomeType(objectDescribe, layoutType);
        return result;
    }

    private void filterIncomeType(IObjectDescribe objectDescribe, String layoutType) {
        if (UdobjConstants.LAYOUT_TYPE_ADD.equals(layoutType) || UdobjConstants.LAYOUT_TYPE_EDIT.equals(layoutType)) {
            if (StringUtils.isNotEmpty(arg.getData_id())) {
                IObjectData objectData = serviceFacade.findObjectData(controllerContext.getUser(), arg.getData_id(), RebateIncomeDetailConstants.API_NAME);
                String refundId = ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.Refund.apiName);
                String orderId = ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.SalesOrder.apiName);
                if (StringUtils.isNotEmpty(refundId) || StringUtils.isNotEmpty(orderId)) {
                    return;
                }
            }
            SelectOneFieldDescribe fieldDescribe = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe("income_type");
            List<ISelectOption> selectOptions = fieldDescribe.getSelectOptions();
            Iterator iterator = selectOptions.iterator();
            while (iterator.hasNext()) {
                ISelectOption option = (ISelectOption) iterator.next();
                if (RebateIncomeTypeEnum.OrderRefund.getValue().equals(option.getValue()) || RebateIncomeTypeEnum.OrderRebate.getValue().equals(option.getValue())) {
                    iterator.remove();
                }
            }
            fieldDescribe.setSelectOptions(selectOptions);
        }
    }

    private static Set<String> fieldToRemoveWhileAdd = Sets.newHashSet(RebateIncomeDetailConstants.Field.SalesOrder.apiName, RebateIncomeDetailConstants.Field.Refund.apiName, RebateIncomeDetailConstants.Field.AvailableRebate.apiName, RebateIncomeDetailConstants.Field.UsedRebate.apiName);
    private static Set<String> fieldToRemoveWhileEdit = Sets.newHashSet(RebateIncomeDetailConstants.Field.SalesOrder.apiName, RebateIncomeDetailConstants.Field.Customer.apiName, RebateIncomeDetailConstants.Field.Amount.apiName, RebateIncomeDetailConstants.Field.StartTime.apiName, RebateIncomeDetailConstants.Field.EndTime.apiName, RebateIncomeDetailConstants.Field.IncomeType.apiName);

    private void filterLayout(Result result, String layoutType, List<IComponent> components) {
        for (IComponent component : components) {
            List<IFieldSection> fieldSections = ((FormComponent) component).getFieldSections();
            for (IFieldSection fieldSection : fieldSections) {
                List<IFormField> formFields = fieldSection.getFields();
                formFields.removeIf(formField -> formField.getFieldName().equals(RebateIncomeDetailConstants.Field.CustomerAccount.apiName));
                fieldSection.setFields(formFields);
            }
        }
        if (!StringUtil.isNullOrEmpty(layoutType)) {
            if (UdobjConstants.LAYOUT_TYPE_ADD.equals(layoutType)) {
                for (IComponent component : components) {
                    FormComponent fc = (FormComponent) component;
                    List<IFieldSection> fields = fc.getFieldSections();
                    for (IFieldSection field : fields) {
                        List<IFormField> formFields = field.getFields();
                        Iterator iterator = formFields.iterator();
                        while (iterator.hasNext()) {
                            IFormField formField = (IFormField) iterator.next();
                            if (fieldToRemoveWhileAdd.contains(formField.getFieldName())) {
                                iterator.remove();
                            }
                        }
                        field.setFields(formFields);
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
                            for (IFormField formField : formFields) {
                                if (fieldToRemoveWhileEdit.contains(formField.getFieldName())) {
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

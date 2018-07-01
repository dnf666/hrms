package com.facishare.crm.customeraccount.predefine.controller;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.FormField;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.TableColumn;
import com.facishare.paas.metadata.impl.ui.layout.component.SimpleComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.IGroupComponent;
import com.facishare.paas.metadata.ui.layout.IRelatedObjectList;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/10/21.
 */
@Slf4j
public class CustomerAccountDetailController extends StandardDetailController {
    List<ITableColumn> prepayTableColumns = Lists.newArrayList();
    List<ITableColumn> rebateTableColumns = Lists.newArrayList();
    List<IFormField> customerAccountHeader = Lists.newArrayList();

    @Override
    public Result doService(Arg arg) {
        return super.doService(arg);
    }

    @Override
    public void before(Arg arg) {
        super.before(arg);
    }

    @Override
    public Result after(Arg arg, Result result) {
        setPrepayTableColumns();
        setRebateTableColumns();
        setCustomerAccountHeader();
        Result result1 = super.after(arg, result);
        try {
            Layout layout = new Layout(result1.getLayout());
            List<IComponent> components = layout.getComponents();
            if (!components.isEmpty()) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.RELATED_OBJECT)) {//"relatedObject"
                        IGroupComponent componentMap = (IGroupComponent) component;
                        List<IComponent> list = componentMap.getChildComponents();
                        IComponent prepayComponent = null;
                        IComponent rebateComponent = null;
                        List<IComponent> sortList = Lists.newArrayList();
                        for (IComponent component1 : list) {
                            if (component1 instanceof IRelatedObjectList) {
                                IRelatedObjectList relatedObjectList = (IRelatedObjectList) component1;
                                String refApiName = relatedObjectList.getRefObjectApiName();
                                if (StringUtils.isNotBlank(refApiName) && refApiName.equals(RebateIncomeDetailConstants.API_NAME)) {
                                    relatedObjectList.setIncludeFields(rebateTableColumns);
                                    rebateComponent = relatedObjectList;
                                    continue;
                                }
                                if (StringUtils.isNotBlank(refApiName) && refApiName.equals(PrepayDetailConstants.API_NAME)) {
                                    relatedObjectList.setIncludeFields(prepayTableColumns);
                                    prepayComponent = relatedObjectList;
                                    continue;
                                }
                            }
                            sortList.add(component1);
                        }
                        if (Objects.nonNull(rebateComponent)) {
                            sortList.add(0, rebateComponent);
                        }
                        if (Objects.nonNull(prepayComponent)) {
                            sortList.add(0, prepayComponent);
                        }
                        componentMap.setChildComponents(sortList);
                    }
                    if (component.getName().equals(LayoutConstants.HEADER_API_NAME)) {
                        SimpleComponent componentMap = (SimpleComponent) component;
                        List<IFieldSection> fieldSections = componentMap.getFieldSections();
                        fieldSections.get(0).setFields(customerAccountHeader);
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.error("layout getComponent error:{}", e);
        }
        return result1;
    }

    private void setCustomerAccountHeader() {
        FormField formField1 = new FormField();
        formField1.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        formField1.setRenderType("select_one");
        formField1.setReadOnly(true);
        formField1.setRequired(true);
        FormField formField = new FormField();
        formField.setFieldName(CustomerAccountConstants.Field.RebateBalance.apiName);
        formField.setRenderType("currency");
        formField.setReadOnly(true);
        formField.setRequired(true);
        FormField formField2 = new FormField();
        formField2.setFieldName(CustomerAccountConstants.Field.Customer.apiName);
        formField2.setRenderType("master_detail");
        formField2.setReadOnly(true);
        formField2.setRequired(true);
        FormField formField3 = new FormField();
        formField3.setFieldName(CustomerAccountConstants.Field.PrepayBalance.apiName);
        formField3.setRenderType("currency");
        formField3.setReadOnly(true);
        formField3.setRequired(true);
        FormField formField4 = new FormField();
        formField4.setFieldName(CustomerAccountConstants.Field.CreditQuota.apiName);
        formField4.setRenderType("currency");
        formField4.setReadOnly(true);
        formField4.setRequired(true);
        //customerAccountHeader.add(formField2);
        customerAccountHeader.add(formField3);
        customerAccountHeader.add(formField);
        customerAccountHeader.add(formField4);
        customerAccountHeader.add(formField1);

    }

    private void setRebateTableColumns() {

        rebateTableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.Amount.label, RebateIncomeDetailConstants.Field.Amount.apiName, "currency"));
        rebateTableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.IncomeType.label, RebateIncomeDetailConstants.Field.IncomeType.apiName, "select_one"));
        rebateTableColumns.add(getTableColumn(SystemConstants.Field.LifeStatus.label, SystemConstants.Field.LifeStatus.apiName, "select_one"));
        rebateTableColumns.add(getTableColumn(RebateIncomeDetailConstants.Field.TransactionTime.label, RebateIncomeDetailConstants.Field.TransactionTime.apiName, "date_time"));
    }

    private void setPrepayTableColumns() {

        prepayTableColumns.add(getTableColumn(PrepayDetailConstants.Field.Amount.label, PrepayDetailConstants.Field.Amount.apiName, "currency"));
        prepayTableColumns.add(getTableColumn(PrepayDetailConstants.Field.IncomeType.label, PrepayDetailConstants.Field.IncomeType.apiName, "select_one"));
        prepayTableColumns.add(getTableColumn(PrepayDetailConstants.Field.OutcomeType.label, PrepayDetailConstants.Field.OutcomeType.apiName, "select_one"));
        prepayTableColumns.add(getTableColumn(SystemConstants.Field.LifeStatus.label, SystemConstants.Field.LifeStatus.apiName, "select_one"));
        prepayTableColumns.add(getTableColumn(PrepayDetailConstants.Field.TransactionTime.label, PrepayDetailConstants.Field.TransactionTime.apiName, "date_time"));
        //prepayTableColumns.add(getTableColumn(SystemConstants.Field.RecordType.label, SystemConstants.Field.RecordType.apiName, "record_type"));
    }

    private TableColumn getTableColumn(String lableName, String name, String renderType) {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setLabelName(lableName);
        tableColumn.setName(name);
        tableColumn.setRenderType(renderType);
        return tableColumn;
    }
}

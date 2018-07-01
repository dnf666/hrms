package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.constant.CasesConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.dto.AggregatedObjDescribeLayoutResult;
import com.facishare.paas.appframework.metadata.dto.DescribeDetailResult;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by luxin on 2018/4/8.
 */
public class CasesDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result doService(Arg arg) {
        Result result = super.doService(arg);

        //当时新建和编辑的时候,将 引用客户/联系人/销售订单的field移除
        //noinspection deprecation
        if (UdobjConstants.LAYOUT_TYPE_ADD.equals(arg.getLayout_type()) || UdobjConstants.LAYOUT_TYPE_EDIT.equals(arg.getLayout_type())) {
            FormComponent formComponent = (FormComponent) LayoutExt.of(result.getLayout().toLayout()).getFormComponent().get().getFormComponent();
            Set<String> specialQuoteFieldName = getSpecialQuoteFieldName(result.getObjectDescribe().toObjectDescribe().getFieldDescribes());
            removeSpecialFieldsFromFormComponent(formComponent, specialQuoteFieldName);
        }

        List<AggregatedObjDescribeLayoutResult> aggregatedObjDescribeLayoutList= Lists.newArrayList();
        fillAggregatedObjDescribeLayoutList(aggregatedObjDescribeLayoutList);
        result.setAggregatedObjDescribeLayoutList(aggregatedObjDescribeLayoutList);

        return result;
    }

    /**
     * 找出引用类型是 预制的客户/联系人/销售订单的字段的名字
     *
     * @param fieldDescribes
     * @return
     */
    private Set<String> getSpecialQuoteFieldName(List<IFieldDescribe> fieldDescribes) {
        Set<String> specialQuoteFieldName = Sets.newHashSet();
        for (IFieldDescribe fieldDescribe : fieldDescribes) {
            String quoteFieldInfo = fieldDescribe.getQuoteField();
            if (quoteFieldInfo != null && (quoteFieldInfo.startsWith("account_id__r") ||
                    quoteFieldInfo.startsWith("contact_id__r") ||
                    quoteFieldInfo.startsWith("sales_order_id__r"))) {
                specialQuoteFieldName.add(fieldDescribe.getApiName());
            }
        }
        return specialQuoteFieldName;
    }


    private void removeSpecialFieldsFromFormComponent(FormComponent formComponent, Set<String> specialQuoteFieldName) {
        List<IFieldSection> fieldSections = Lists.newArrayListWithExpectedSize(formComponent.getFieldSections().size());
        for (IFieldSection iFieldSection : formComponent.getFieldSections()) {
            List<IFormField> formFields = Lists.newArrayListWithExpectedSize(iFieldSection.getFields().size());
            for (IFormField iFormField : iFieldSection.getFields()) {
                if (!specialQuoteFieldName.contains(iFormField.getFieldName())) {
                    formFields.add(iFormField);
                }
            }
            iFieldSection.setFields(formFields);
        }
    }


    private void fillAggregatedObjDescribeLayoutList(List<AggregatedObjDescribeLayoutResult> aggregatedObjDescribeLayoutList) {
        for (String refObjectApiName : CasesConstants.REF_OBJECT_API_NAMES) {
            DescribeDetailResult refObjectDescribe = serviceFacade.findDescribeByApiName(controllerContext.getRequestContext(),
                    refObjectApiName, false, null, "default__c", false, false, false, null);

            ILayout layout = serviceFacade.findLayoutByApiName(controllerContext.getUser(),
                    CasesConstants.OBJECT_API_NAME_2_OBJECT_CASES_LIST_LAYOUT_API_NAME.get(refObjectApiName), refObjectApiName);

            AggregatedObjDescribeLayoutResult tmpResult = new AggregatedObjDescribeLayoutResult();

            tmpResult.setFieldApiName(CasesConstants.REF_OBJECT_API_NAME_2_DB_KEY_WORD.get(refObjectApiName));
            if (refObjectDescribe != null) {
                tmpResult.setObjectDescribe(refObjectDescribe.getObjectDescribe());
            }
            tmpResult.setLayout(layout);

            aggregatedObjDescribeLayoutList.add(tmpResult);
        }
        ILayout historyCasesLayout = serviceFacade.findLayoutByApiName(controllerContext.getUser(),
                CasesConstants.OBJECT_API_NAME_2_OBJECT_CASES_LIST_LAYOUT_API_NAME.get(Utils.CASES_API_NAME), Utils.CASES_API_NAME);
        AggregatedObjDescribeLayoutResult tmpResult = new AggregatedObjDescribeLayoutResult();
        tmpResult.setFieldApiName("history_cases");
        tmpResult.setLayout(historyCasesLayout);
        aggregatedObjDescribeLayoutList.add(tmpResult);
    }

}

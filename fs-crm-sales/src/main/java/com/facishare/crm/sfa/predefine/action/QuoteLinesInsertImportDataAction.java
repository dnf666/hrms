package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.utilities.validator.QuoteImportValidator;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;

import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import java.util.List;

public class QuoteLinesInsertImportDataAction extends StandardInsertImportDataAction {

    private final PriceBookCommonService priceBookCommonService = SpringUtil.getContext().getBean(PriceBookCommonService.class);

    @Override
    protected void customValidate(List<ImportData> dataList) {
        super.customValidate(dataList);
        List<ImportError> errorList = Lists.newArrayList();

        QuoteImportValidator.customValidate(objectDescribe,actionContext,priceBookCommonService,serviceFacade,errorList,dataList);

        mergeErrorList(errorList);
    }
}

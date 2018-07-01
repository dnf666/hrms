package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.utilities.validator.QuoteImportValidator;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportDataAction;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import java.util.List;

public class QuoteUpdateImportDataAction extends StandardUpdateImportDataAction {
    PriceBookCommonService priceBookCommonService = SpringUtil.getContext().getBean(PriceBookCommonService.class);

    @Override
    protected void customValidate(List<ImportData> dataList) {
        super.customValidate(dataList);

        List<ImportError> errorList = Lists.newArrayList();
        QuoteImportValidator.validateOpportunityInAccount(actionContext,serviceFacade,errorList,dataList);

        if(QuoteValidator.enablePriceBook(objectDescribe) ){
            QuoteImportValidator.validateAccountPriceBook(actionContext,priceBookCommonService,errorList,dataList);
        }

        mergeErrorList(errorList);
    }
}

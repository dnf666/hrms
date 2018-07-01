package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;

public class QuoteRelatedListController extends StandardRelatedListController {

    @Override
    protected Result after(Arg arg, Result result) {
        //为兼容IOS BUG，选择报价单时，清除价目表ID
        if(!QuoteValidator.enablePriceBook(objectDescribe)){
         result.getDataList().forEach(x->
                 x.put(QuoteConstants.QuoteField.PRICEBOOKID.getApiName(),null)
         );
        }
        return result;
    }
}

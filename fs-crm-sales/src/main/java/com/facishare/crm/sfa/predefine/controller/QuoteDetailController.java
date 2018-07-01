package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;

public class QuoteDetailController extends StandardDetailController {

    @Override
    protected Result after(Arg arg, Result result) {
        super.after(arg, result);

        //为兼容IOS BUG，未开启价目表时，清除价目表ID数据
        if(!QuoteValidator.enablePriceBook(result.getDescribe().toObjectDescribe())){
            result.getData().put(QuoteConstants.QuoteField.PRICEBOOKID.getApiName(),null);
            result.getData().put(QuoteConstants.QuoteField.PRICEBOOKNAME.getApiName(),null);
        }
        return result;
    }
}

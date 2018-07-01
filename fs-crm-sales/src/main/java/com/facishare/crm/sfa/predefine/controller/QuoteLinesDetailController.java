package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.api.IObjectData;

public class QuoteLinesDetailController extends StandardDetailController {

    @Override
    protected Result after(Arg arg, Result result) {
        addPriceBookId();
        return super.after(arg, result);
    }

    /**
     * 报价单明细详情中增加价目表ID
     */
    private void addPriceBookId(){
        if(arg.isFromRecycleBin()){
            return;
        }

        IObjectData quoteData = this.serviceFacade.findObjectData(
                getControllerContext().getUser(),
                data.get(QuoteConstants.QuoteField.QUOTEID.getApiName()).toString(),
                SFAPreDefineObject.Quote.getApiName() );
        if(quoteData!=null){
            data.set(QuoteConstants.QuoteField.PRICEBOOKID.getApiName(),
                    String.valueOf(quoteData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName())));
        }
    }
}

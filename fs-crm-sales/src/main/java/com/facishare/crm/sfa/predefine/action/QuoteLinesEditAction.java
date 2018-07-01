package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.util.SpringUtil;

public class QuoteLinesEditAction extends StandardEditAction {
    PriceBookCommonService priceBookCommonService = SpringUtil.getContext().getBean(PriceBookCommonService.class);

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        if(QuoteValidator.enablePriceBook(objectDescribe)) {
            QuoteValidator.validateProductInPriceBook(
                    getActionContext(),
                    priceBookCommonService,
                    this.serviceFacade,
                    objectData);
        }

        QuoteValidator.validateProductIsRepeated(
                getActionContext(),
                this.serviceFacade,
                objectData
        );
    }
}

package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.util.SpringUtil;

public class QuoteLinesAddAction extends StandardAddAction {
    PriceBookCommonService priceBookCommonService = SpringUtil.getContext().getBean(PriceBookCommonService.class);

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        if(QuoteValidator.enablePriceBook(objectDescribe)){
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

        setStandardPriceBook();
    }

    /**
     * 没有开启价目表时，报价单明细自动填充标准价目表产品。
     */
    private void setStandardPriceBook(){
        if(!QuoteValidator.enablePriceBook(objectDescribe)){
            //价目表产品ID=产品ID+租户ID
            objectData.set(
                    QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName(),
                    String.valueOf(objectData.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()))
                            .concat(actionContext.getTenantId())
            );
        }
    }
}

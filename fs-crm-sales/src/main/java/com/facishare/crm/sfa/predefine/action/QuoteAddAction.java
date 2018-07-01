package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.api.IObjectData;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class QuoteAddAction extends StandardAddAction {

    private final PriceBookCommonService priceBookCommonService = SpringUtil.getContext().getBean(PriceBookCommonService.class);
    private final PriceBookService priceBookService = SpringUtil.getContext().getBean(PriceBookService.class);

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        if (QuoteValidator.enablePriceBook(objectDescribe)) {
            QuoteValidator.validateAccountPriceBook(getActionContext(), priceBookCommonService, objectData);
            QuoteValidator.validateProductInPriceBook(priceBookCommonService, getActionContext().getTenantId(), objectData, detailObjectData);
        }
        setStandardPriceBook();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        super.after(arg, result);

        //为兼容IOS BUG，未开启价目表时，清除价目表ID数据
        if (!QuoteValidator.enablePriceBook(objectDescribe)) {
            result.getObjectData().put(QuoteConstants.QuoteField.PRICEBOOKID.getApiName(), null);
            result.getObjectData().put(QuoteConstants.QuoteField.PRICEBOOKNAME.getApiName(), null);
        }
        return result;
    }

    /**
     * 没有开启价目表时，报价单自动填充标准价目表。
     */
    private void setStandardPriceBook() {
        if (!QuoteValidator.enablePriceBook(objectDescribe)) {

            IObjectData standardPriceBook = priceBookService.getStandardPriceBook(actionContext.getUser());
            if (standardPriceBook == null) {
                log.error("standardPriceBook no found. actionContext {}", actionContext);
                return;
            }
            objectData.set(QuoteConstants.QuoteField.PRICEBOOKID.getApiName(), standardPriceBook.getId());
            List<IObjectData> quoteLinesDatas = detailObjectData.get(SFAPreDefineObject.QuoteLines.getApiName());
            //价目表产品ID=产品ID+租户ID
            quoteLinesDatas.forEach(quoteLinesData ->
                    quoteLinesData.set(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName(),
                            String.valueOf(quoteLinesData.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()))
                                    .concat(actionContext.getTenantId())));
        }
    }
}

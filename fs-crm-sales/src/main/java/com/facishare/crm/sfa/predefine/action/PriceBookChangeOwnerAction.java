package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardChangeOwnerAction;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by luxin on 2017/12/7.
 */
@Slf4j
public class PriceBookChangeOwnerAction extends StandardChangeOwnerAction {
    private final PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        IObjectData standardPriceBook = priceBookService.getStandardPriceBook(actionContext.getUser());
        if (standardPriceBook == null) {
            log.error("standardPriceBook no found. actionContext {}", actionContext);
            return;
        }
        arg.getData().removeIf(changeOwnerData -> {
            if (changeOwnerData.getObjectDataId() != null && changeOwnerData.getObjectDataId().equals(standardPriceBook.getId().toString())) {
                if (arg.getData().size() == 1) {
                    throw new ValidateException("标准价目表不许更换负责人");
                } else {
                    bulkOpResult = BulkOpResult.builder().failObjectDataList(Lists.newArrayList(standardPriceBook)).failReason("标准价目表不许更换负责人").build();
                    return Boolean.TRUE;
                }
            } else {
                return Boolean.FALSE;
            }
        });
    }


}

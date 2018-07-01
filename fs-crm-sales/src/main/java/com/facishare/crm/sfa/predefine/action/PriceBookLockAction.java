package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PriceBookService;

import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.BaseObjectLockAction;
import com.facishare.paas.appframework.core.predef.action.StandardLockAction;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by luxin on 2017/12/7.
 */
@Slf4j
public class PriceBookLockAction extends StandardLockAction {
    private final PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");

    @Override
    protected void before(BaseObjectLockAction.Arg arg) {
        super.before(arg);

        IObjectData standardPriceBook = priceBookService.getStandardPriceBook(actionContext.getUser());
        if (standardPriceBook == null) {
            log.error("standardPriceBook no found. actionContext {}", actionContext);
            return;
        }
        arg.getDataIds().removeIf(o -> {
            if (o.equals(standardPriceBook.getId() == null ? null : standardPriceBook.getId().toString())) {
                if (arg.getDataIds().size() == 1) {
                    throw new ValidateException("标准价目表不许锁定");
                }
                bulkOpResult = BulkOpResult.builder()
                        .failObjectDataList(Lists.newArrayList(standardPriceBook))
                        .failReason("标准价目表不许锁定").build();
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        });
    }


}

package com.facishare.crm.promotion.predefine.action;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.google.common.collect.Lists;

public class PromotionBulkRecoverAction extends StandardBulkRecoverAction {
    @Override
    protected List<String> getFuncPrivilegeCodes() {
        List<String> codes = super.getFuncPrivilegeCodes();
        if (CollectionUtils.isEmpty(codes)) {
            return Lists.newArrayList(ObjectAction.RECOVER.getActionCode());
        }
        return codes;
    }
}

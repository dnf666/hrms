package com.facishare.crm.promotion.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.google.common.collect.Lists;

public class AdvertisementAddAction extends StandardAddAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        this.objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(actionContext.getUser().getUserId()));
    }
}

package com.facishare.crm.customeraccount.predefine.action;

import java.util.Objects;

import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;

public class RebateUseRuleAddAction extends StandardAddAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        Object usedMaxAmount = objectData.get(RebateUseRuleConstants.Field.UsedMaxAmount.apiName);
        Object usedMaxPrecent = objectData.get(RebateUseRuleConstants.Field.UsedMaxPrecent.apiName);
        if (Objects.isNull(usedMaxAmount) && Objects.isNull(usedMaxPrecent)) {
            throw new ValidateException("{可使用返利最高金额}和{可使用返利最高比例}字段不能全为空");
        }
    }
}

package com.facishare.crm.promotion.predefine.privilege;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class PromotionRuleFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.CREATE.getActionCode(),

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.PRINT.getActionCode(),

            ObjectAction.ADD_EVENT.getActionCode(),

            ObjectAction.SALE_RECORD.getActionCode(),

            ObjectAction.SEND_MAIL.getActionCode(),

            ObjectAction.DISCUSS.getActionCode(),

            ObjectAction.SCHEDULE.getActionCode(),

            ObjectAction.REMIND.getActionCode());

    @Override
    public String getApiName() {
        return PromotionRuleConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put(CommonConstants.ORDER_MANAGER_ROLE, Collections.unmodifiableList(supportActionCodes));
        actionCodeMap.put(CommonConstants.PRODUCT_MANAGER_ROLE, Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

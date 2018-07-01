package com.facishare.crm.customeraccount.predefine.privilege;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class RebateOutcomeDetailFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(

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
        return RebateOutcomeDetailConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000002", Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }

}

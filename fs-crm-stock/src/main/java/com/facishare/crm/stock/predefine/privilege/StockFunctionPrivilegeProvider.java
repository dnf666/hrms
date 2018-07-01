package com.facishare.crm.stock.predefine.privilege;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StockFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode(),

            ObjectAction.PRINT.getActionCode());

    private final static List<String> viewActionCodes = Lists.newArrayList(

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode());



    @Override
    public String getApiName() {
        return StockConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put(CommonConstants.ORDER_FINANCE_ROLE, Collections.unmodifiableList(viewActionCodes));
        actionCodeMap.put(CommonConstants.ORDER_MANAGER_ROLE, Collections.unmodifiableList(viewActionCodes));
        actionCodeMap.put(CommonConstants.GOODS_SENDING_PERSON_ROLE, Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

package com.facishare.crm.customeraccount.predefine.privilege;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CustomerAccountFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.BATCH_IMPORT.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode(),

            ObjectAction.PRINT.getActionCode(),

            ObjectAction.ADD_EVENT.getActionCode(),

            ObjectAction.SALE_RECORD.getActionCode(),

            ObjectAction.DISCUSS.getActionCode(),

            ObjectAction.SCHEDULE.getActionCode(),

            ObjectAction.REMIND.getActionCode());

    @Override
    public String getApiName() {
        return CustomerAccountConstants.API_NAME;
    }

    //回款财务
    private String paymentFinacailRole = "00000000000000000000000000000002";
    //销售人员
    private String salesRole = "00000000000000000000000000000015";

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000015", Collections.unmodifiableList(supportActionCodes));
        actionCodeMap.put("00000000000000000000000000000002", Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

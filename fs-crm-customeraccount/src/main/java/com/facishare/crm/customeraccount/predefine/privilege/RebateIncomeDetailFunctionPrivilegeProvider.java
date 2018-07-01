package com.facishare.crm.customeraccount.predefine.privilege;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RebateIncomeDetailFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.CREATE.getActionCode(),

            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.BATCH_IMPORT.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode(),

            ObjectAction.INVALID.getActionCode(),

            ObjectAction.RECOVER.getActionCode(),

            ObjectAction.CHANGE_OWNER.getActionCode(),

            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),

            ObjectAction.DELETE.getActionCode(),

            ObjectAction.LOCK.getActionCode(),

            ObjectAction.UNLOCK.getActionCode(),

            ObjectAction.START_BPM.getActionCode(),

            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),

            ObjectAction.STOP_BPM.getActionCode(),

            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),

            ObjectAction.PRINT.getActionCode(),

            ObjectAction.ADD_EVENT.getActionCode(),

            ObjectAction.SALE_RECORD.getActionCode(),

            ObjectAction.SEND_MAIL.getActionCode(),

            ObjectAction.DISCUSS.getActionCode(),

            ObjectAction.SCHEDULE.getActionCode(),

            ObjectAction.REMIND.getActionCode()

    //            ObjectAction.BATCH_IMPORT.getActionCode()

    );

    @Override
    public String getApiName() {
        return RebateIncomeDetailConstants.API_NAME;
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

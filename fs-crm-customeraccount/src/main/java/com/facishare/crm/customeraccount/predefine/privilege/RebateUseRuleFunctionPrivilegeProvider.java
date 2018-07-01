package com.facishare.crm.customeraccount.predefine.privilege;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;

@Component
public class RebateUseRuleFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.CREATE.getActionCode(),

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.DELETE.getActionCode(),

            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.INVALID.getActionCode(),

            ObjectAction.RECOVER.getActionCode(),

            ObjectAction.PRINT.getActionCode(),

            ObjectAction.CHANGE_OWNER.getActionCode(),

            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),

            ObjectAction.LOCK.getActionCode(),

            ObjectAction.UNLOCK.getActionCode(),

            ObjectAction.START_BPM.getActionCode(),

            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),

            ObjectAction.STOP_BPM.getActionCode(),

            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),

            ObjectAction.ADD_EVENT.getActionCode(),

            ObjectAction.MODIFYLOG_RECOVER.getActionCode(),

            ObjectAction.SALE_RECORD.getActionCode(),

            ObjectAction.SEND_MAIL.getActionCode(),

            ObjectAction.DISCUSS.getActionCode(),

            ObjectAction.SCHEDULE.getActionCode(),

            ObjectAction.REMIND.getActionCode(),

            ObjectAction.BATCH_IMPORT.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode()

    );

    @Override
    public String getApiName() {
        return RebateUseRuleConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }
}

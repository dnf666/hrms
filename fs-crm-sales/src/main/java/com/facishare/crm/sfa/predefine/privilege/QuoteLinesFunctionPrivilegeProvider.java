package com.facishare.crm.sfa.predefine.privilege;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class QuoteLinesFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {

    private final static List<String> salesSupportActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),
            ObjectAction.RELATE.getActionCode(),
            ObjectAction.INVALID.getActionCode(),
            ObjectAction.RECOVER.getActionCode(),
            ObjectAction.BATCH_IMPORT.getActionCode(),
            ObjectAction.BATCH_EXPORT.getActionCode(),
            ObjectAction.CHANGE_OWNER.getActionCode(),
            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),
            ObjectAction.START_BPM.getActionCode(),
            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),
            ObjectAction.STOP_BPM.getActionCode(),
            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),
            ObjectAction.PRINT.getActionCode(),
            ObjectAction.INTELLIGENTFORM.getActionCode(),
            ObjectAction.LOCK.getActionCode(),
            ObjectAction.UNLOCK.getActionCode(),
            ObjectAction.MODIFYLOG_RECOVER.getActionCode()
    );

    @Override
    public String getApiName() {
        return SFAPreDefineObject.QuoteLines.getApiName();
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000015", Collections.unmodifiableList(salesSupportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

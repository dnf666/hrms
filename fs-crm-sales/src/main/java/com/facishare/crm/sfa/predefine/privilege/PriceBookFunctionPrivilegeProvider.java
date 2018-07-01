package com.facishare.crm.sfa.predefine.privilege;


import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2017/12/7.
 */
@Component
public class PriceBookFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),
            ObjectAction.INVALID.getActionCode(),
            ObjectAction.RECOVER.getActionCode(),
            ObjectAction.DELETE.getActionCode(),
            ObjectAction.BATCH_IMPORT.getActionCode(),
            ObjectAction.BATCH_EXPORT.getActionCode(),
            ObjectAction.CHANGE_OWNER.getActionCode(),
            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),
            ObjectAction.START_BPM.getActionCode(),
            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),
            ObjectAction.STOP_BPM.getActionCode(),
            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),
            ObjectAction.PRINT.getActionCode(),
            ObjectAction.LOCK.getActionCode(),
            ObjectAction.UNLOCK.getActionCode(),
            ObjectAction.MODIFYLOG_RECOVER.getActionCode()
    );


    @Override
    public String getApiName() {
        return Utils.PRICE_BOOK_API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000024", Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }


}

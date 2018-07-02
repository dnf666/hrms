package com.facishare.crm.requisitionnote.predefine.privilege;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 13/03/2018
 */
@Component
public class RequisitionNoteFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    //Todo 具体功能权限需要再核对一下
    private final static List<String> supportActionCodes = Lists.newArrayList(

            ObjectAction.DELETE.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.CREATE.getActionCode(),

            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode(),

            ObjectAction.INVALID.getActionCode(),

            ObjectAction.RECOVER.getActionCode(),

            ObjectAction.PRINT.getActionCode(),

            ObjectAction.CHANGE_OWNER.getActionCode(),

            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),

            ObjectAction.START_BPM.getActionCode(),

            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),

            ObjectAction.STOP_BPM.getActionCode(),

            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),

            ObjectAction.LOCK.getActionCode(),

            ObjectAction.UNLOCK.getActionCode()
    );

    @Override
    public String getApiName() {
        return RequisitionNoteConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put(CommonConstants.GOODS_SENDING_PERSON_ROLE, Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }

}

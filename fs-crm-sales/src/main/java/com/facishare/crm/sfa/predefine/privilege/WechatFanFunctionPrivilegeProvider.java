package com.facishare.crm.sfa.predefine.privilege;


import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;

/**
 * Created by guom on 2018/5/30.
 */
@Component
public class WechatFanFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {

    @Override
    public String getApiName() {
        return Utils.WECHAT_FAN_API_NAME;
    }

    private final static List<String> supportActionCodes = Lists.newArrayList(
    		ObjectAction.DELETE.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),
            ObjectAction.BATCH_EXPORT.getActionCode(),
            ObjectAction.INVALID.getActionCode(),
            ObjectAction.RECOVER.getActionCode(),
            ObjectAction.PRINT.getActionCode(),
            ObjectAction.CHANGE_OWNER.getActionCode(),
            ObjectAction.ADD_TEAM_MEMBER.getActionCode(),
            ObjectAction.EDIT_TEAM_MEMBER.getActionCode(),
            ObjectAction.DELETE_TEAM_MEMBER.getActionCode(),
            ObjectAction.RELATE.getActionCode(),
            ObjectAction.BULK_RELATE.getActionCode(),
            ObjectAction.BULK_DISRELATE.getActionCode(),
            ObjectAction.BULK_DELETE.getActionCode(),
            ObjectAction.BULK_INVALID.getActionCode(),
            ObjectAction.BULK_RECOVER.getActionCode(),
            ObjectAction.START_BPM.getActionCode(),
            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),
            ObjectAction.STOP_BPM.getActionCode(),
            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),
            ObjectAction.INTELLIGENTFORM.getActionCode(),
            ObjectAction.LOCK.getActionCode(),
            ObjectAction.UNLOCK.getActionCode(),
            ObjectAction.MODIFYLOG_RECOVER.getActionCode()
    );


    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

}

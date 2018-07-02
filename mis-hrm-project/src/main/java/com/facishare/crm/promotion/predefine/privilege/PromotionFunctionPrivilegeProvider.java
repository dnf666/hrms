package com.facishare.crm.promotion.predefine.privilege;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class PromotionFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
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

            ObjectAction.LOCK.getActionCode(),

            ObjectAction.UNLOCK.getActionCode(),

            ObjectAction.START_BPM.getActionCode(),

            ObjectAction.VIEW_ENTIRE_BPM.getActionCode(),

            ObjectAction.STOP_BPM.getActionCode(),

            ObjectAction.CHANGE_BPM_APPROVER.getActionCode(),

            ObjectAction.ADD_EVENT.getActionCode(),

            ObjectAction.SALE_RECORD.getActionCode(),

            ObjectAction.SEND_MAIL.getActionCode(),

            ObjectAction.DISCUSS.getActionCode(),

            ObjectAction.SCHEDULE.getActionCode(),

            //TODO 修改记录-恢复 签到 签退 待收款 打电话 关联 解除关联

            ObjectAction.REMIND.getActionCode());

    @Override
    public String getApiName() {
        return PromotionConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put(CommonConstants.PRODUCT_MANAGER_ROLE, Collections.unmodifiableList(supportActionCodes));
        actionCodeMap.put(CommonConstants.ORDER_MANAGER_ROLE, Collections.unmodifiableList(supportActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

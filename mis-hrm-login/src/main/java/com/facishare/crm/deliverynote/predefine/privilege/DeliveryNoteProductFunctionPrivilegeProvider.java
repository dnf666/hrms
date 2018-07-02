package com.facishare.crm.deliverynote.predefine.privilege;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 发货单对象的权限
 * Created by chenzs on 2018/1/10.
 */
@Component
public class DeliveryNoteProductFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    /**
     * 对象的权限
     */
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.DELETE.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode()
            );

    /**
     * "订单管理员" 和 "订单财务" 的权限
     */
    private final static List<String> orderManagerAndOrderFinanceActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.VIEW_LIST.getActionCode());

    @Override
    public String getApiName() {
        return DeliveryNoteProductObjConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        //发货人员
        actionCodeMap.put(CommonConstants.GOODS_SENDING_PERSON_ROLE, Collections.unmodifiableList(supportActionCodes));
        //订单管理员
        actionCodeMap.put(CommonConstants.ORDER_MANAGER_ROLE, Collections.unmodifiableList(orderManagerAndOrderFinanceActionCodes));
        //订单财务
        actionCodeMap.put(CommonConstants.ORDER_FINANCE_ROLE, Collections.unmodifiableList(orderManagerAndOrderFinanceActionCodes));
        //订货人员
        actionCodeMap.put(CommonConstants.ORDERING_PERSON_ROLE, Collections.unmodifiableList(orderManagerAndOrderFinanceActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}
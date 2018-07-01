package com.facishare.crm.stock.predefine.privilege;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/10.
 */
@Component
public class GoodsReceivedNoteProductFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.DELETE.getActionCode(),

            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.VIEW_LIST.getActionCode(),

            ObjectAction.CREATE.getActionCode(),

            ObjectAction.UPDATE.getActionCode()

    );

    @Override
    public String getApiName() {
        return GoodsReceivedNoteProductConstants.API_NAME;
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


package com.facishare.crm.checkins.privilege;

import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsm on 2018/4/24/0024.
 */
@Component
public class CheckinsFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {

    @Override
    public String getApiName() {
        return "CheckinsObj" ;
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000026", Collections.unmodifiableList(super.getSupportedActionCodes()));
        return Collections.unmodifiableMap(actionCodeMap);
    }

}

package com.facishare.crm.checkins.privilege;

import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsm on 2018/4/24/0024.
 * 初始化方法
 */
@Component
public class CheckinsImgFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {

    @Override
    public String getApiName() {
        return "CheckinsImgObj" ;
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000026", Collections.unmodifiableList(super.getSupportedActionCodes()));
        return Collections.unmodifiableMap(actionCodeMap);
    }

}

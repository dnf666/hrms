package com.facishare.crm.electronicsign.predefine.privilege;

import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 签署方对象的权限
 * Created by chenzs on 2018/4/18.
 */
@Component
public class SignerFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    /**
     * 对象的权限
     */
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),
            ObjectAction.BATCH_EXPORT.getActionCode()
            );

    @Override
    public String getApiName() {
        return SignerObjConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        return Collections.unmodifiableMap(actionCodeMap);
    }
}
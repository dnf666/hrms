package com.facishare.crm.electronicsign.predefine.privilege;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 内部签章认证对象的权限
 * Created by chenzs on 2018/5/4.
 */
@Component
public class InternalSignCertifyFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    /**
     * 对象的权限
     */
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),

//          ObjectAction.INVALID.getActionCode(),
//          ObjectAction.RECOVER.getActionCode(),
//          ObjectAction.DELETE.getActionCode(),

//          ObjectAction.BATCH_IMPORT.getActionCode(),  需要后面在刷，王凡他们刷过
            ObjectAction.BATCH_EXPORT.getActionCode()
            );

    @Override
    public String getApiName() {
        return InternalSignCertifyObjConstants.API_NAME;
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
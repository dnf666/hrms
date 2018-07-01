package com.facishare.crm.electronicsign.predefine.privilege;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 客户签章认证对象的权限
 * Created by chenzs on 2018/4/17.
 */
@Component
public class AccountSignCertifyFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    /**
     * 对象的权限
     */
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.INVALID.getActionCode(),
            ObjectAction.RECOVER.getActionCode(),

            ObjectAction.DELETE.getActionCode(),

//          ObjectAction.BATCH_IMPORT.getActionCode(),  需要后面在刷，王凡他们刷过
            ObjectAction.BATCH_EXPORT.getActionCode()
            );

    /**
     * "销售人员"的权限
     */
    private final static List<String> salesmenActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.VIEW_DETAIL.getActionCode(),

            ObjectAction.CREATE.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),

            ObjectAction.INVALID.getActionCode(),

            ObjectAction.BATCH_EXPORT.getActionCode()
    );

    @Override
    public String getApiName() {
        return AccountSignCertifyObjConstants.API_NAME;
    }

    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        //销售人员
        actionCodeMap.put(CommonConstants.SALE_PERSON_ROLE, Collections.unmodifiableList(salesmenActionCodes));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}
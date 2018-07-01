package com.facishare.crm.sfa.predefine.privilege;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by renlb on 2018/5/2.
 */
@Component
public class GoalValueFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    private final static List<String> supportActionCodes = Lists.newArrayList(
            ObjectAction.VIEW_LIST.getActionCode(),
            ObjectAction.UPDATE.getActionCode(),
            ObjectAction.BATCH_IMPORT.getActionCode(),
            ObjectAction.BATCH_EXPORT.getActionCode()
    );

    @Override
    public String getApiName() {
        return SFAPreDefineObject.GoalValue.getApiName();
    }


    @Override
    public List<String> getSupportedActionCodes() {
        return Collections.unmodifiableList(supportActionCodes);
    }

    @Override
    public Map<String, List<String>> getCustomInitRoleActionCodes() {
        Map<String, List<String>> actionCodeMap = Maps.newHashMap();
        actionCodeMap.put("00000000000000000000000000000015", Collections.unmodifiableList(getSupportedActionCodes()));
        return Collections.unmodifiableMap(actionCodeMap);
    }
}

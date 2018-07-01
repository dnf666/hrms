package com.facishare.crm.sfa.predefine.privilege;


import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.model.DefaultFunctionPrivilegeProvider;

import org.springframework.stereotype.Component;

/**
 * 盘点对象的权限定制
 * Created by quzf on 2018/3/13.
 */
@Component
public class InventoryFunctionPrivilegeProvider extends DefaultFunctionPrivilegeProvider {
    @Override
    public String getApiName() {
        return "InventoryObj";
    }

    @Override
    public String getFunctionCodeFromActionCode(String apiName, String actionCode) {
        if (actionCode.equals(ObjectAction.VIEW_LIST.getActionCode())) {
            //盘点的查看标识
            return "21011";
        }
        return super.getFunctionCodeFromActionCode(apiName, actionCode);
    }

}

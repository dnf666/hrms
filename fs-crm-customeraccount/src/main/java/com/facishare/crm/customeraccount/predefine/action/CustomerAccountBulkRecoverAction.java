package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class CustomerAccountBulkRecoverAction extends StandardBulkRecoverAction {
    private CustomerAccountManager customerAccountManager;
    private CrmManager crmManager;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        if (!RequestUtil.isFromInner(actionContext)) {
            List<IObjectData> customerAccountDatas = customerAccountManager.listInvalidDataByIds(actionContext.getUser(), arg.getIdList());
            List<String> customerIds = customerAccountDatas.stream().map(objectData -> ObjectDataUtil.getReferenceId(objectData, CustomerAccountConstants.Field.Customer.apiName)).collect(Collectors.toList());
            List<IObjectData> customerObjectDatas = crmManager.listInvalidCustomerByIds(actionContext.getUser(), customerIds);
            Map<String, String> customerNameMap = customerObjectDatas.stream().collect(Collectors.toMap(objectData -> objectData.getId(), IObjectData::getName));
            Map<String, String> cusANameCusNameMap = Maps.newHashMap();
            customerAccountDatas.forEach(customerAccountData -> {
                String customerId = ObjectDataUtil.getReferenceId(customerAccountData, CustomerAccountConstants.Field.Customer.apiName);
                cusANameCusNameMap.put(customerAccountData.getName(), customerNameMap.get(customerId));
            });
            throw new ValidateException(String.format("客户账户{%s}关联客户{%s}，请恢复客户！", Joiner.on(",").join(cusANameCusNameMap.keySet()), Joiner.on(",").join(cusANameCusNameMap.values())));
        }
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }

    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }
}

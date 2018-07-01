package com.facishare.crm.customeraccount.predefine.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.manager.result.FuncResult;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.HttpUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PrivilegeManager {

    public boolean initCrmManagerAllFunction(String tenantId, Integer userId, String objectApiName) {
        String url = ConfigCenter.funcUrl + "/v1/definedObjectInit";
        Map<String, String> headers = getHeaders();
        Map<String, Object> body = Maps.newHashMap();
        body.put("tenantId", tenantId);
        body.put("userId", userId);
        body.put("apiName", objectApiName);
        try {
            FuncResult<Boolean> result = HttpUtil.post(url, headers, body, FuncResult.class);
            log.debug("tenantId:{},initCrmManagerAllFunction result:{}", tenantId, result);
            return result.isSuccess();
        } catch (IOException e) {
            log.warn("", e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.INIT_FUNC_EXCEPTION, e.getMessage());
        }
    }

    public boolean addFuncByFuncInfo(String tenantId, Map<String, String> funcCode2DescMap) {
        String url = ConfigCenter.funcUrl + "/updatefuncrest/addfuncbyfuncinfo";
        Map<String, String> headers = getHeaders();
        Map<String, Object> body = Maps.newHashMap();
        body.put("tenantIds", Lists.newArrayList(tenantId));
        body.put("funcCode2DescMap", funcCode2DescMap);
        try {
            Boolean result = HttpUtil.post(url, headers, body, Boolean.class);
            log.warn("tenantId:{},addFuncByFuncInfo result:{}", tenantId, result);
            return result;
        } catch (IOException e) {
            log.warn("", e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.ADD_FUNC_TO_OBJECT_EXCEPTION, e.getMessage());
        }
    }

    public boolean addFunc2Role(String tenantId, String roleCode, List<String> funcCodes) {
        String url = ConfigCenter.funcUrl + "/commonprivilege/addfunc2role";
        Map<String, String> headers = getHeaders();
        Map<String, Object> body = Maps.newHashMap();
        body.put("tenantId", tenantId);
        body.put("roleCode", roleCode);
        body.put("funcCodes", funcCodes);
        try {
            FuncResult<String> funcResult = HttpUtil.post(url, headers, body, FuncResult.class);
            return funcResult.isSuccess();
        } catch (IOException e) {
            log.warn("", e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.ADD_FUNC_TO_ROLE, e.getMessage());
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }
}

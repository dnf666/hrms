package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.facishare.crm.sfa.predefine.service.model.VersionPrivilegeCheckArg;
import com.facishare.crm.sfa.predefine.version.VersionService;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 分版&权限校验接口类
 */
@ServiceModule("version_privilege")
@Component
@Slf4j
public class VersionPrivilegeService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    VersionService versionService;

    @ServiceMethod("check")
    public Map<String, Object> check(VersionPrivilegeCheckArg.Arg arg, ServiceContext context) {
        User user = context.getUser();
        if (CollectionUtils.isEmpty(arg.getApiNames())) {
            throw new ValidateException("apiNames不能为空");
        }
        if (CollectionUtils.isEmpty(arg.getActionCodes())) {
            throw new ValidateException("actionCodes不能为空");
        }
        Set<String> apiNames = arg.getApiNames();
        Set<String> actionCodes = arg.getActionCodes();
        Set<String> newApiNames = Sets.newHashSet(apiNames);
        //过滤分版
        versionService.filterSupportObj(user.getTenantId(), newApiNames);
        //获取权限
        Map<String, Map<String, Boolean>> checkPrivilege = functionPrivilegeService.batchFunPrivilegeCheck(user, new ArrayList<>(newApiNames),
                Lists.newArrayList(arg.getActionCodes()));
        //填充checkPrivilege中没有检验的对象
        apiNames.stream().forEach(apiName -> checkPrivilege.computeIfAbsent(apiName, k -> {
            Map<String, Boolean> map = Maps.newHashMap();
            actionCodes.forEach(code -> map.put(code, false));
            return map;
        }));
        Map map = Maps.newHashMap();
        map.put("result", checkPrivilege);
        return map;
    }

}

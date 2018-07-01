package com.facishare.crm.customeraccount.util;

import java.util.Map;

import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Maps;

public class HeaderUtil {

    public static Map<String, String> getApprovalHeader(String tenantId, String fsUserId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", fsUserId);
        headers.put("x-tenant-id", tenantId);
        return headers;
    }

    public static Map<String, String> getApprovalHeader(User user) {
        return getApprovalHeader(user.getTenantId(), user.getUserId());
    }

    public static Map<String, String> getCrmHeader(String tenantId, String userId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        return headers;
    }
}

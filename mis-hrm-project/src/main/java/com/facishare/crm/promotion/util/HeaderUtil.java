package com.facishare.crm.promotion.util;

import java.util.Map;

import com.google.common.collect.Maps;

public class HeaderUtil {
    public static Map<String, String> getCrmHeader(String tenantId, String userId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        return headers;
    }
}

package com.facishare.crm.deliverynote.predefine.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenzs on 2018/1/22.
 */
public class DeliveryNoteUtil {
    public static Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

    public static Map<String, String> getHeadersWithLength(String tenantId, String userId) {
        Map<String, String> headers = getHeaders(tenantId, userId);
        headers.put("Content-Length", "0");
        return headers;
    }
}
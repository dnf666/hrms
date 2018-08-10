package com.mis.hrm.util;

import java.util.HashMap;
import java.util.Map;

public  class ToMap {
    private static Map<String, Object> result = null;
    public static Map<String, Object> toMap(int code, String message, Object object){
        result = new HashMap<>();
        result.put("code", code);
        result.put("msg", message);
        result.put("object", object);
        return result;
    }
}

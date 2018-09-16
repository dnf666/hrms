package com.mis.hrm.util;

import java.util.HashMap;
import java.util.Map;

public  class ToMap {
    private ToMap(){}
    public static Map<String, Object> toMap(int code, String message, Object object){
        Map<String, Object> result;
        result = new HashMap<>();
        result.put("code", code);
        result.put("msg", message);
        result.put("object", object);
        return result;
    }

    public static Map<String, Object> toSuccessMap(Object o){
        return toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, o);
    }

    public static Map<String, Object> toFalseMap(Object o){
        return toMap(ConstantValue.FALSE_CODE, ConstantValue.FALSE, o);
    }

    public static Map<String, Object> toFalseMap(String msg){
        return toMap(ConstantValue.FALSE_CODE, msg, null);
    }

    public static Map<String, Object> toFalseMapByServerError(){
        return toMap(ConstantValue.SEVER_ERROR_CODE, ConstantValue.BIND_EXCEPTION, null);
    }
}

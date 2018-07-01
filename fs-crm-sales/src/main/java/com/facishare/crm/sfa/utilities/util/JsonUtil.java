package com.facishare.crm.sfa.utilities.util;

import org.jetbrains.annotations.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by lilei on 2017/7/28.
 */
public class JsonUtil {
    @Nullable
    public static String getStringWithoutNPE(String fieldName, JSONObject jsonObject) {
        String rtnValue = "";
        java.lang.Object obj = jsonObject.get(fieldName);
        if (obj != null) {
            rtnValue = obj.toString();
        }
        return rtnValue;
    }

    public static String toJsonWithNullValues(Object o) {
        return JSON.toJSONString(o, SerializerFeature.WriteMapNullValue);
    }
}

package com.facishare.crm.sfa.utilities.common.convert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

/**
 * Created by lilei on 2017/8/8.
 */
public class ConvertUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JSONObject processOwnerID(JSONObject jsonObject, String fieldName) {
        Object OwnerID = jsonObject.get(fieldName);
        if (OwnerID != null) {
            if (OwnerID instanceof JSONArray) {
                JSONArray owners = jsonObject.getJSONArray(fieldName);
                if (owners != null && owners.size() > 0) {
                    jsonObject.put(fieldName, owners.getIntValue(0));
                } else {
                    jsonObject.put(fieldName, 0);//没传则不更新
                }
            }
        }
        return jsonObject;
    }

    public static void mergeMDForSalesOrder(ObjectDataDocument object_data, Map<String, List<ObjectDataDocument>> details, String detailKey) {

        if (object_data == null) {
            return;
        }
        if (details != null) {
            List<ObjectDataDocument> list = details.get(detailKey);
            object_data.put(detailKey, list);
        }
    }

}

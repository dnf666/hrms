package com.facishare.crm.goal.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.goal.service.dto.GoalCheckIndex;

import java.io.InputStream;
import java.util.LinkedHashMap;

public abstract class CheckIndexMapping {

    private static LinkedHashMap map;

    static {
        init();
    }

    public static LinkedHashMap getCheckIndices() {
        return map;
    }

    public static GoalCheckIndex getGoalCheckIndex(String checkObjectApiName){
        LinkedHashMap goalCheckIndexMap = CheckIndexMapping.getCheckIndices();
        JSONObject jsonObject = (JSONObject)goalCheckIndexMap.get(checkObjectApiName);
        return jsonObject.toJavaObject(GoalCheckIndex.class);
    }

    private static void init() {
        try {
            InputStream inputStream = CheckIndexMapping.class.getResourceAsStream("/check_index_list.json");
            map = JSON.parseObject(inputStream, LinkedHashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("CheckIndexMapping init failed", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(getCheckIndices());
    }
}

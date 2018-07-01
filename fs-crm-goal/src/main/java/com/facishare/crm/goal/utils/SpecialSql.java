package com.facishare.crm.goal.utils;

import com.facishare.paas.appframework.common.util.Tuple;
import com.facishare.paas.appframework.metadata.ResourceLoader;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * Created by renlb on 2018/4/18.
 */
public class SpecialSql {
    private static Map<String, String> listMap = Maps.newHashMap();

    static {
        init();
    }

    public static String getQuerySql(String apiName) {
        String s = listMap.get(apiName);
        return listMap.get(apiName);
    }

    private static void init() {
        Set<Tuple<String, String>> mapping = ResourceLoader.loadMapping(getResourceName());
        mapping.forEach(x -> {
            listMap.put(x.getKey(), x.getValue());
        });
    }

    private static String getResourceName() {
        return "special_sql";
    }

    public static void main(String[] args) {
        System.out.println(getQuerySql("RuleFilter"));
    }
}

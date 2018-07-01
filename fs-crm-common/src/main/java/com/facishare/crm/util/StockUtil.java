package com.facishare.crm.util;

import com.facishare.paas.metadata.impl.search.Operator;


import com.facishare.paas.metadata.impl.search.Where;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/17.
 */
public class StockUtil {
    //获取启用的仓库过滤条件
    public static List<LinkedHashMap> getEnableWarehouseWheres() {
        List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Map> filters = Lists.newArrayList();
        Map<String, Object> filter = Maps.newHashMap();
        filter.put("field_name", "is_enable");
        filter.put("operator", Operator.EQ.name());
        filter.put("field_values", Lists.newArrayList("1"));//1表示启用
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);
        return wheres;
    }

    //获取已上架产品过滤条件
    public static List<LinkedHashMap> getOnSaleProductWheres() {
        List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Map> filters = Lists.newArrayList();
        Map<String, Object> filter = Maps.newHashMap();
        filter.put("field_name", "product_status");
        filter.put("operator", Operator.EQ.name());
        filter.put("field_values", Lists.newArrayList("1"));//1表示已上架
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);
        return wheres;
    }
}

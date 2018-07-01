package com.facishare.crm.stock.util;

import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.Where;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by linchf on 2018/1/9.
 */
public class StockUtils {
    public static final String ACCOUNT_API_NAME = "AccountObj";

    public static final String WAREHOUSE_RELATED_SALES_ORDER = "shipping_warehouse_sales_order_list";
    public static final String WAREHOUSE_RELATED_RETURN_ORDER = "return_warehouse_return_order_list";

    public static final String CHECK_STOCK_WARNING = "stock_warning";
    //获取启用的仓库过滤条件
    public static List<LinkedHashMap> getEnableWarehouseWheres() {
        List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Map> filters = Lists.newArrayList();
        Map<String, Object> filter = Maps.newHashMap();
        filter.put("field_name", WarehouseConstants.Field.Is_Enable.apiName);
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

    //获取http header  适合带参数
    public static Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

    //获取http header  适合不带参数
    public static Map<String, String> getHeadersWithLength(String tenantId, String userId) {
        Map<String, String> headers = getHeaders(tenantId, userId);
        headers.put("Content-Length", "0");
        return headers;
    }

    //获取仓库导入模板
    public static List<IFieldDescribe> getWarehouseTemplateHeader(List<IFieldDescribe> headerFieldList) {
        List<IFieldDescribe> removeFieldDescribe = Lists.newArrayList();
        removeFieldDescribe.addAll(headerFieldList.stream().filter(describe -> Objects.equals(WarehouseConstants.Field.Account_range.getApiName(), describe.getApiName())
                || Objects.equals("extend_obj_data_id", describe.getApiName())).collect(Collectors.toList()));
        headerFieldList.removeAll(removeFieldDescribe);
        return headerFieldList;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

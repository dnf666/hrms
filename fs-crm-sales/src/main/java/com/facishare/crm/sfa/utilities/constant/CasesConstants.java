package com.facishare.crm.sfa.utilities.constant;

import com.facishare.crm.openapi.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/4/3.
 */
public class CasesConstants {
    public static final int OFFSET = 0;
    //模糊查询查询数量限制
    public static final int FUZZY_SEARCH_LIMIT = 10;
    //精确查询查询数量限制
    public static final int ACCURATE_SEARCH_LIMIT = 1;

    //工单特殊相关对象列表
    public static final List<String> REF_OBJECT_API_NAMES;
    public static final Map<String, String> REF_OBJECT_API_NAME_2_SEARCH_KEY_WORD;

    public static final Map<String, String> REF_OBJECT_API_NAME_2_DB_NAME;
    public static final Map<String, String> REF_OBJECT_API_NAME_2_DB_KEY_WORD;
    public static final Map<String, List<String>> REF_OBJECT_API_NAME_2_DB_RETURN_KEYS;
    //对象apiName对应的对象的工单layout的apiName
    public static final Map<String, String> OBJECT_API_NAME_2_OBJECT_CASES_LIST_LAYOUT_API_NAME;


    static {
        List<String> tmp = Lists.newArrayListWithCapacity(3);
        tmp.add(Utils.ACCOUNT_API_NAME);
        tmp.add(Utils.CONTACT_API_NAME);
        //tmp.add(Utils.SALES_ORDER_API_NAME);
        REF_OBJECT_API_NAMES = Collections.unmodifiableList(tmp);
    }


    static {
        Map<String, String> tmp = Maps.newHashMapWithExpectedSize(3);
        tmp.put(Utils.ACCOUNT_API_NAME, "customer");
        tmp.put(Utils.CONTACT_API_NAME, "contact");
        //tmp.put(Utils.SALES_ORDER_API_NAME, "customer_trade");
        REF_OBJECT_API_NAME_2_DB_NAME = Collections.unmodifiableMap(tmp);
    }


    static {
        Map<String, String> tmp = Maps.newHashMapWithExpectedSize(3);
        tmp.put(Utils.ACCOUNT_API_NAME, "name");
        tmp.put(Utils.CONTACT_API_NAME, "name");
        //tmp.put(Utils.SALES_ORDER_API_NAME, "trade_code");
        REF_OBJECT_API_NAME_2_SEARCH_KEY_WORD = Collections.unmodifiableMap(tmp);
    }

    static {
        Map<String, List<String>> tmp = Maps.newHashMap();
        tmp.put(Utils.ACCOUNT_API_NAME, Lists.newArrayList("customer_id as _id", "name"));
        tmp.put(Utils.CONTACT_API_NAME, Lists.newArrayList("contact_id ad _id", "name"));
        //tmp.put(Utils.SALES_ORDER_API_NAME, Lists.newArrayList("customer_trade_id as _id", "trade_code as name", "customer.customer_id as account_id", "customer.name as account_id__r"));
        REF_OBJECT_API_NAME_2_DB_RETURN_KEYS = Collections.unmodifiableMap(tmp);
    }


    static {
        Map<String, String> tmp = Maps.newHashMap();
        tmp.put(Utils.ACCOUNT_API_NAME, "account_id");
        tmp.put(Utils.CONTACT_API_NAME, "contact_id");
        //tmp.put(Utils.SALES_ORDER_API_NAME, "sales_order_id");
        REF_OBJECT_API_NAME_2_DB_KEY_WORD = Collections.unmodifiableMap(tmp);
    }


    static {
        Map tmp = Maps.newHashMapWithExpectedSize(4);
        tmp.put(Utils.ACCOUNT_API_NAME, "layout_AccountObj_caseslist_layout");
        tmp.put(Utils.CONTACT_API_NAME, "layout_ContactObj_caseslist_layout");
        //tmp.put(Utils.SALES_ORDER_API_NAME, "layout_SalesOrderObj_caseslist_layout");
        tmp.put(Utils.CASES_API_NAME, "layout_CasesObj_caseslist_layout");
        OBJECT_API_NAME_2_OBJECT_CASES_LIST_LAYOUT_API_NAME = Collections.unmodifiableMap(tmp);
    }


    private CasesConstants() {
    }

}

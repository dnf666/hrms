package com.facishare.crm.deliverynote.util;

import com.github.autoconf.ConfigFactory;

/**
 * Created by xujf on 2017/10/16.
 */
public class ConfigCenter {
    public static String Kdniao_EBusinessID;
    public static String Kdniao_AppKey;
    public static String Kdniao_OrderDistinguishApiReqURL;
    public static String Kdniao_TrackQueryApiReqURL;
    public static String changeFieldRequire_TenantIds;  //55422;55423 这种形式
    public static String Transfer_TenantIds;  //53409;55424 这种格式
    public static String Transfer_TenantIds_SalesOrder_Add_Fields;  //53409;55424 这种格式

    /**
     * 订货通通知URL
     */
    public static String ORDER_NOTIFY_URL = "http://10.113.32.48:8009/sail-local/order/notify";

    public static boolean IS_TEST = false;
    public static long IS_TEST_SLEEP_TIME = 30000;

    static {
        ConfigFactory.getInstance().getConfig("fs-crm-deliverynote", config -> {
            Kdniao_EBusinessID = config.get("Kdniao_EBusinessID", "1321415");
            Kdniao_AppKey = config.get("Kdniao_AppKey", "b7bed609-feaa-414c-bd4f-972eb44677ec");
            Kdniao_OrderDistinguishApiReqURL = config.get("Kdniao_OrderDistinguishApiReqURL", "http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx");
            Kdniao_TrackQueryApiReqURL = config.get("Kdniao_TrackQueryApiReqURL", "http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx");

            changeFieldRequire_TenantIds = config.get("changeFieldRequire_TenantIds", changeFieldRequire_TenantIds);
            ORDER_NOTIFY_URL = config.get("ORDER_NOTIFY_URL", ORDER_NOTIFY_URL);
            Transfer_TenantIds = config.get("Transfer_TenantIds", Transfer_TenantIds);
            Transfer_TenantIds_SalesOrder_Add_Fields = config.get("Transfer_TenantIds_SalesOrder_Add_Fields", Transfer_TenantIds_SalesOrder_Add_Fields);
            IS_TEST = config.getBool("IS_TEST", IS_TEST);
            IS_TEST_SLEEP_TIME = config.getLong("IS_TEST_SLEEP_TIME", IS_TEST_SLEEP_TIME);
        });
    }
}

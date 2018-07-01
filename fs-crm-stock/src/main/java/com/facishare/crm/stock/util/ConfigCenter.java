package com.facishare.crm.stock.util;

import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IConfigFactory;

/**
 * @author liangk
 * @date 15/03/2018
 */
public class ConfigCenter {
    private static IConfigFactory factory = ConfigFactory.getInstance();

    /**
     * paas框架内部请求连接地址
     */
    public static String PAAS_FRAMEWORK_URL = "http://10.113.32.68:8234/API/v1/inner/object/";

    /**
     * 新建入库单中入库单产品最大数量
     */
    public static Integer GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM = 50;

    public static String  ENABLE_STOCK_TENANT_IDS = "";

    /**
     * STOCK_LOG 库存记录表最早的记录
     */
    public static String EARLIEST_STOCK_LOG_CREATE_TIME = "1523546195660";

    public static Integer NUMBER_FIELD_DECIMAL_PLACES = 5;

    public static String ALLOW_MODIFIED_SALES_ORDER_IDS = "";

    public static String SUPPER_ADMIN_ID = "1.6661";

    public static String DISABLE_STOCK_TENANT_IDS = "";
    static {
        factory.getConfig("fs-crm-stock", config -> {
            GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM = config.getInt("GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM", GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM);
            PAAS_FRAMEWORK_URL = config.get("paas_framework_url", PAAS_FRAMEWORK_URL);
            ENABLE_STOCK_TENANT_IDS = config.get("enableStock_TenantIds", ENABLE_STOCK_TENANT_IDS);
            SUPPER_ADMIN_ID = config.get("supper_admin_id", SUPPER_ADMIN_ID);
            DISABLE_STOCK_TENANT_IDS = config.get("disable_stock_tenant_ids", DISABLE_STOCK_TENANT_IDS);
            EARLIEST_STOCK_LOG_CREATE_TIME = config.get("earliest_stock_log_create_time", EARLIEST_STOCK_LOG_CREATE_TIME);
            ALLOW_MODIFIED_SALES_ORDER_IDS = config.get("allow_modified_sales_order_ids", ALLOW_MODIFIED_SALES_ORDER_IDS);
            NUMBER_FIELD_DECIMAL_PLACES = config.getInt("NUMBER_FIELD_DECIMAL_PLACES", NUMBER_FIELD_DECIMAL_PLACES);
        });
    }
}

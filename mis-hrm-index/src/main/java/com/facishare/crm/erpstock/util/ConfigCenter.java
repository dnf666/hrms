package com.facishare.crm.erpstock.util;

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

    public static String ENABLE_STOCK_TENANT_IDS = "";

    public static String SUPPER_ADMIN_ID = "1.6661";

    public static String DISABLE_STOCK_TENANT_IDS = "";
    static {
        factory.getConfig("fs-crm-stock", config -> {
            GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM = config.getInt("GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM", GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM);
            PAAS_FRAMEWORK_URL = config.get("paas_framework_url", PAAS_FRAMEWORK_URL);
            ENABLE_STOCK_TENANT_IDS = config.get("enableStock_TenantIds", ENABLE_STOCK_TENANT_IDS);
            SUPPER_ADMIN_ID = config.get("supper_admin_id", SUPPER_ADMIN_ID);
            DISABLE_STOCK_TENANT_IDS = config.get("disable_stock_tenant_ids", DISABLE_STOCK_TENANT_IDS);
        });
    }
}

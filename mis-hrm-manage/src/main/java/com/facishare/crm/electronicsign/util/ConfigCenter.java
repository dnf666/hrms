package com.facishare.crm.electronicsign.util;

import com.alibaba.fastjson.JSONObject;
import com.github.autoconf.ConfigFactory;
import sun.rmi.runtime.Log;

/**
 * Created by xujf on 2017/10/16.
 */
public class ConfigCenter {
    /**
     * 上上签URL
     */
    public static String BEST_SIGN_URL = "https://openapi.bestsign.info/openapi/v2";
    /**
     * 上上签开发者ID
     */
    public static String BEST_SIGN_DEVELOPER_ID;

    /**
     * 使用自定义的'对账单'apiName的租户信息
     * {"53409":"object_22473__c", "53408":"object_22474__c"}
     */
    public static JSONObject TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME;

    public static String X_FS_EI_FOR_ELECTRON_SIGN;

    /**
     * 个人配额价格（单位：分）
     */
    public static long INDIVIDUAL_QUOTA_PRICE;
    /**
     * 企业配额价格（单位：分）
     */
    public static long ENTERPRISE_QUOTA_PRICE;

    /**
     * 更新usedQuota重试次数
     */
    public static int UPDATE_USED_QUOTA_TRY_TIMES = 3;

    static {
        ConfigFactory.getInstance().getConfig("fs-crm-electronic-sign", config -> {
            BEST_SIGN_URL = config.get("BEST_SIGN_URL", BEST_SIGN_URL);
            BEST_SIGN_DEVELOPER_ID = config.get("BEST_SIGN_DEVELOPER_ID", BEST_SIGN_DEVELOPER_ID);
            TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME = JSONObject.parseObject(config.get("TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME"));
            INDIVIDUAL_QUOTA_PRICE = config.getLong("INDIVIDUAL_QUOTA_PRICE", INDIVIDUAL_QUOTA_PRICE);
            ENTERPRISE_QUOTA_PRICE = config.getLong("ENTERPRISE_QUOTA_PRICE", ENTERPRISE_QUOTA_PRICE);
            UPDATE_USED_QUOTA_TRY_TIMES = config.getInt("UPDATE_USED_QUOTA_TRY_TIMES", UPDATE_USED_QUOTA_TRY_TIMES);
        });

        ConfigFactory.getInstance().getConfig("fs-open-app-manage-biz", config -> {
            X_FS_EI_FOR_ELECTRON_SIGN = config.get("X_FS_EI_FOR_ELECTRON_SIGN", X_FS_EI_FOR_ELECTRON_SIGN);
        });
    }

    /**
     * 是否使用自定义的'对账单'apiName
     */
    public static boolean isUseCustomAccountStatementObjApiName(String tenantId) {
        JSONObject jsonObject = ConfigCenter.TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME;
        return jsonObject.get(tenantId) != null;
    }

    /**
     * 获取该租户使用的自定义的'对账单'apiName
     */
    public static String getCustomAccountStatementObjApiName(String tenantId) {
        JSONObject jsonObject = ConfigCenter.TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME;
        return (String) jsonObject.get(tenantId);
    }
}
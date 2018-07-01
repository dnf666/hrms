package com.facishare.crm.electronicsign.util;

import com.alibaba.fastjson.JSONObject;
import com.github.autoconf.ConfigFactory;

import static com.facishare.crm.electronicsign.util.ConfigCenter.*;

public class ConfigCenterTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }
    public static void main(String[] args) {
//        ConfigFactory.getInstance().getConfig("fs-crm-electronic-sign", config -> {
//            BEST_SIGN_URL = config.get("BEST_SIGN_URL", BEST_SIGN_URL);
////            BEST_SIGN_DEVELOPER_ID = config.get("BEST_SIGN_DEVELOPER_ID", BEST_SIGN_DEVELOPER_ID);
////            TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME = JSONObject.parseObject(config.get("TENANT_HAS_CUSTOM_ACCOUNT_STATEMENT_OBJ_API_NAME"));
////            INDIVIDUAL_QUOTA_PRICE = config.getLong("INDIVIDUAL_QUOTA_PRICE", INDIVIDUAL_QUOTA_PRICE);
////            ENTERPRISE_QUOTA_PRICE = config.getLong("ENTERPRISE_QUOTA_PRICE", ENTERPRISE_QUOTA_PRICE);
//        });
        System.out.println(ConfigCenter.INDIVIDUAL_QUOTA_PRICE);
    }

}
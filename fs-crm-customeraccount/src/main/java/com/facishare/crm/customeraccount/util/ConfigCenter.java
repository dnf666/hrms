package com.facishare.crm.customeraccount.util;

import com.github.autoconf.ConfigFactory;

/**
 * Created by xujf on 2017/10/16.
 */
public class ConfigCenter {
    public static String crmUrl;
    public static String funcUrl;
    public static int initInBehindThreshold;
    public static int queryCount;
    public static String taskMachine;
    public static int batchCreateSize;
    public static boolean queryCustomersFormPg;
    public static boolean instanceStateReady;
    public static String frameworkUrl;

    static {
        ConfigFactory.getInstance().getConfig("fs-crm-customeraccount", config -> {
            initInBehindThreshold = config.getInt("INIT_IN_BEHIND_THRESHOLD");
            funcUrl = config.get("funcUrl");
            queryCount = config.getInt("queryCount", 100);
            taskMachine = config.get("task.machine");
            batchCreateSize = config.getInt("batch.create.size", 500);
            crmUrl = config.get("crm_url");
            queryCustomersFormPg = config.getBool("query_customer_from_pg", false);
            instanceStateReady = config.getBool("instanceStateReady", false);
            frameworkUrl = config.get("framework_url");
        });
    }
}

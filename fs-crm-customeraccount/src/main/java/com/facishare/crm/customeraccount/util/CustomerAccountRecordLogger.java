package com.facishare.crm.customeraccount.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class CustomerAccountRecordLogger {
    private static Logger recordLogger = LoggerFactory.getLogger("customeraccount.record");

    public static void logPrepay(String customerId, BigDecimal addPrepayBalance, BigDecimal addPrepayLockedBalance, BigDecimal newPrepayBalance, BigDecimal newPrepayLockedBalance, String info, Exception e) {
        if (e != null) {
            recordLogger.info("[Pre-e]{}-Add[{},{}]-New[{},{}]-[{}]-[{}]", customerId, addPrepayBalance, addPrepayLockedBalance, newPrepayBalance, newPrepayLockedBalance, info, e.getMessage());
        } else {
            recordLogger.info("[Pre-s]{}-Add[{},{}]-New[{},{}]-[{}]", customerId, addPrepayBalance, addPrepayLockedBalance, newPrepayBalance, newPrepayLockedBalance, info);
        }
    }

    public static String generatePrepayInfo(String prepayId, String oldLifeStatus, String newLifeStatus) {
        return String.format("gen-pre:%s,(%s->%s)", prepayId, oldLifeStatus, newLifeStatus);
    }

    public static String generateRebateInfo(String rebateId, String action) {
        return String.format("gen-Rebate:%s,(action:%s)", rebateId, action);
    }

    public static void logRebate(String customerId, BigDecimal addRebateBalance, BigDecimal addRebateLockedBalance, BigDecimal newRebateBalance, BigDecimal newRebateLockedBalance, String info, Exception e) {
        if (e != null) {
            recordLogger.info("[Rebate-e]{}-Add[{},{}]-New[{},{}]-[{}]-[{}]", customerId, addRebateBalance, addRebateLockedBalance, newRebateBalance, newRebateLockedBalance, info, e.getMessage());
        } else {
            recordLogger.info("[Rebate-s]{}-Add[{},{}]-New[{},{}]-[{}]", customerId, addRebateBalance, addRebateLockedBalance, newRebateBalance, newRebateLockedBalance, info);
        }
    }

    public static String generateRebateInfo(String rebateId, String oldLifeStatus, String newLifeStatus) {
        return String.format("gen-Rebate:%s,(%s->%s)", rebateId, oldLifeStatus, newLifeStatus);
    }
}

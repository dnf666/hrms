package com.facishare.crm.customeraccount.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.metadata.api.IObjectData;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectDataUtil {

    public void assertNotNullOrEmpty(IObjectData data, String key, String message) {
        Object value = data.get(key);
        if (value == null) {
            throw new ValidateException(message);
        }
        if (value instanceof String) {
            if (StringUtil.isNullOrEmpty((String) value)) {
                throw new ValidateException(message);
            }
        }
    }

    /**
     * 获取引用类型id，引用类型字段有可能为Map或者string类型
     * @param data
     * @param key
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getReferenceId(IObjectData data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return String.valueOf(((Map) value).get(IObjectData.ID));
        }
        return String.valueOf(value);
    }

    public static boolean isCurrentTimeActive(Date start, Date end) {
        end = DateUtil.getTomorrowDate(end);
        Date date = new Date();
        if (start == null) {
            if (end != null) {
                return !end.before(date);
            }
            return true;
        } else {
            if (start.after(date)) {
                return false;
            }
            if (end == null) {
                return true;
            }
            return !end.before(date);
        }
    }

    public static BigDecimal transferStringToDouble(IObjectData objectData, String fieldApiName) {
        return objectData.get(fieldApiName, BigDecimal.class);
    }

    //有效时间内　审批通过的排序数据列表
    public static List<IObjectData> sort(List<IObjectData> list) {
        List<IObjectData> list1 = list.stream().filter(objectData -> {
            Date start = objectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
            Date end = objectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
            return isCurrentTimeActive(start, end);
        }).collect(Collectors.toList());
        Collections.sort(list1, (o1, o2) -> {
            return compareTo(o1, o2);
        });
        return list1;
    }

    public static int compareTo(IObjectData positive, IObjectData negative) {
        Date pStart = positive.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date pEnd = positive.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        BigDecimal pAmount = transferStringToDouble(positive, RebateIncomeDetailConstants.Field.Amount.apiName);

        Date nStart = negative.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date nEnd = negative.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        BigDecimal nAmount = transferStringToDouble(negative, RebateIncomeDetailConstants.Field.Amount.apiName);

        if (pEnd.before(nEnd)) {
            return -1;
        } else if (pEnd.after(nEnd)) {
            return 1;
        } else {
            if (pStart.before(nStart)) {
                return -1;
            } else if (pStart.after(nStart)) {
                return 1;
            } else {
                if (pAmount.compareTo(nAmount) == -1) {
                    return -1;
                } else if (pAmount.compareTo(nAmount) == 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    public static BigDecimal getBigDecimal(IObjectData objectData, String fieldApiName) {
        Object value = objectData.get(fieldApiName);
        if (value == null || String.valueOf(value).length() == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(value));
    }

}

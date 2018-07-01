package com.facishare.crm.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author liangk
 * @date 21/03/2018
 */
@Slf4j
public class ObjectUtil {

    //http://wiki.firstshare.cn/pages/viewpage.action?pageId=59344930
    public static Map<String, Object> buildConfigMap() {
        Map<String, Object> configMap = Maps.newHashMap();

        Map<String, Object> recordTypeConfigMap = Maps.newHashMap();
        recordTypeConfigMap.put("add", 0);
        recordTypeConfigMap.put("assign", 0);
        configMap.put("record_type", recordTypeConfigMap);

        return configMap;
    }

    public static Map<String, Object> buildConfigMap(boolean isModifyField,
                                                     boolean isModifyLayout,
                                                     boolean isModifyRecordType,
                                                     boolean isModifyCascade,
                                                     boolean isModifyEdit) {
        Map<String, Object> configMap = Maps.newHashMap();

        if (isModifyField) {
            Map<String, Object> fieldsConfigMap = Maps.newHashMap();
            fieldsConfigMap.put("add", 0);
            configMap.put("fields", fieldsConfigMap);
        }

        if (isModifyLayout) {
            Map<String, Object> layoutConfigMap = Maps.newHashMap();
            layoutConfigMap.put("add", 0);
            layoutConfigMap.put("assign", 0);
            configMap.put("layout", layoutConfigMap);
        }

        if (isModifyRecordType) {
            Map<String, Object> recordTypeConfigMap = Maps.newHashMap();
            recordTypeConfigMap.put("add", 0);
            recordTypeConfigMap.put("assign", 0);
            configMap.put("record_type", recordTypeConfigMap);
        }

        if (isModifyCascade) {
            Map<String, Object> cascadeConfigMap = Maps.newHashMap();
            cascadeConfigMap.put("add", 0);
            configMap.put("cascade", cascadeConfigMap);
        }

        if (isModifyEdit) {
            configMap.put("edit", 0);
        }
        return configMap;
    }

    public static Map<String, Object> buildFieldOptionConfigMap() {
        Map<String, Object> configMap = Maps.newHashMap();

        configMap.put("edit", 1);
        configMap.put("remove", 0);
        configMap.put("enable", 0);

        return configMap;
    }
}

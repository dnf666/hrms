package com.facishare.crm.sfa.utilities.common.convert;

import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RangeVerify {
    private static final String AND = "and";
    private static final String OR = "or";

    public boolean verifyConditions(IObjectDescribe describe, IObjectData objectData, JSONObject rangeObj) {
        if (objectData == null) {
            return false;

        }
        String type = rangeObj.getString("type");
        JSONArray conditionArr = rangeObj.getJSONArray("conditions");
        boolean result = false;
        for (Object o : conditionArr) {
            JSONObject obj = (JSONObject) o;
            if (obj.containsKey("conditions")) {
                result = verifyConditions(describe, objectData, obj);
            } else {
                try {
                    result = verifyCondition(describe, objectData, obj);
                } catch (Exception e) {
                    result = false;
                    log.error("verifyCondition error,tenantId {}", objectData.getTenantId(), e);
                }
            }
            if (result && type.equals(OR)) {
                break;
            }
            if (!result && type.equals(AND)) {
                break;
            }
        }
        return result;
    }

    /**
     * 1: 'EQ', //等于 2: 'N', //不等于 3: 'GT', //大于 4: 'GTE', //大于等于 5: 'LT', //小于 6: 'LTE', //小于等于 7:
     * 'LIKE', //包含 8: 'NLIKE', //不包含 9: 'IS', //为空 10: 'ISN', //不为空
     */
    private boolean verifyCondition(IObjectDescribe describe, IObjectData objectData, JSONObject condition) {
        String vfyFieldName = condition.getJSONObject("left").getString("expression");
        //如果describe描述中不包含该字段，则默认返回false，不通过
        if (!describe.containsField(vfyFieldName)) {
            return false;
        }
        IFieldDescribe field = describe.getFieldDescribe(vfyFieldName);
        //禁用字段默认返回false
        if (field == null || !field.isActive()) {
            return false;
        }

        JSONObject right = condition.getJSONObject("right");
//        String vfyFieldType = right.getJSONObject("type").getString("name");
        Object vfyValueObj = right.get("value");
        String vfyValue = vfyValueObj instanceof List ? ((List) vfyValueObj).get(0).toString() : right.getString("value");
        String vfyType = condition.getString("type");
        Object fieldValue = objectData.get(vfyFieldName);
        switch (vfyType) {
            case "EQ":
                if (fieldValue != null) {
                    if (fieldValue instanceof List) {
                        //多选字段，相等时的处理
                        if (vfyValueObj instanceof List) {
                            return CollectionUtils.isEqualCollection((List) fieldValue, (List) vfyValueObj);
                        } else {
                            return CollectionUtils.isEqualCollection((List) fieldValue, Lists.newArrayList(vfyValue));
                        }
                    } else if (fieldValue.toString().equals(vfyValue)) {
                        return true;
                    }
                }
                return false;
            case "N":
                if (fieldValue != null) {
                    if (fieldValue instanceof List) {
                        //多选字段，相等时的处理
                        if (vfyValueObj instanceof List) {
                            return !CollectionUtils.isEqualCollection((List) fieldValue, (List) vfyValueObj);
                        } else {
                            return !CollectionUtils.isEqualCollection((List) fieldValue, Lists.newArrayList(vfyValue));
                        }
                    } else if (!fieldValue.toString().equals(vfyValue)) {
                        return true;
                    }
                }
                return false;
            case "LIKE":
                if (fieldValue == null) {
                    return false;
                }
                if (fieldValue instanceof List) {
                    List fieldValueList = (List) fieldValue;
                    if (fieldValueList.contains(vfyValue)) {
                        return true;
                    }
                } else if (fieldValue.toString().indexOf(vfyValue) != -1) {
                    return true;
                }
                return false;
            case "NLIKE":
                if (fieldValue == null) {
                    return false;
                }
                if (fieldValue instanceof List) {
                    List fieldValueList = (List) fieldValue;
                    if (!fieldValueList.contains(vfyValue)) {
                        return true;
                    }
                } else if (fieldValue.toString().indexOf(vfyValue) == -1) {
                    return true;
                }
                return false;
           /* case "hasValue":
                if (fieldValue != null && fieldValue instanceof List) {
                    List fieldValueList = (List) fieldValue;
                    if (fieldValueList.contains(vfyValue)) {
                        return true;
                    }
                }
                return false;
            case "hasNoValue":
                if (fieldValue != null && fieldValue instanceof List) {
                    List fieldValueList = (List) fieldValue;
                    if (!fieldValueList.contains(vfyValue)) {
                        return true;
                    }
                }
                return false;*/
            case "IS":
                return fieldValue == null || StringUtils.isBlank(fieldValue.toString());
            case "ISN":
                return fieldValue != null && StringUtils.isNotBlank(fieldValue.toString());
            case "GT":
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    return new Double(fieldValue.toString()) > new Double(vfyValue.toString());
                }
                return false;
            case "GTE":
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    return new Double(fieldValue.toString()) >= new Double(vfyValue.toString());
                }
                return false;
            case "LT":
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    return new Double(fieldValue.toString()) < new Double(vfyValue.toString());
                }
                return false;
            case "LTE":
                if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                    return new Double(fieldValue.toString()) <= new Double(vfyValue.toString());
                }
                return false;
            default:
                return false;
        }
    }

}

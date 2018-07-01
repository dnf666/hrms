package com.facishare.crm.manager;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.util.ObjectDataFieldConvertUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerRangeManager {
    @Autowired
    private ServiceFacade serviceFacade;

    public ObjectDataDocument packData(User user, ObjectDataDocument dataDocument, String fieldApiName) {
        String tenantId = user.getTenantId();
        Object accountRange = dataDocument.get(fieldApiName);
        if (accountRange != null && StringUtils.isNotBlank(accountRange.toString())) {
            JSONObject accountRangeObj = null;
            if (accountRange instanceof Map) {
                accountRangeObj = new JSONObject((Map) accountRange);
            } else if (accountRange instanceof String) {
                accountRangeObj = JSON.parseObject(accountRange.toString());
            } else {
                log.error("fillFieldUserScope error,accountRange type instanceof error");
                return dataDocument;
            }
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                IObjectDescribe describe = this.serviceFacade.findObject(tenantId, Utils.ACCOUNT_API_NAME);
                packAccountRange(user, describe, accountRangeObj.getJSONObject("value"));
                dataDocument.put(fieldApiName, accountRangeObj.toJSONString());
            }
        }
        return dataDocument;
    }

    private void packAccountRange(User user, IObjectDescribe describe, JSONObject rangeObj) {
        JSONArray conditionArr = rangeObj.getJSONArray("conditions");
        for (Object o : conditionArr) {
            JSONObject obj = (JSONObject) o;
            if (obj.containsKey("conditions")) {
                packAccountRange(user, describe, obj);
            } else {
                JSONObject left = obj.getJSONObject("left");
                String fieldName = left.getString("expression");
                JSONObject right = obj.getJSONObject("right");
                Object fieldValue = right.get("value");
                right.put("label", "--");
                left.put("label", "--");
                IFieldDescribe fieldDescribe = describe.getFieldDescribe(fieldName);
                if (fieldDescribe == null || !fieldDescribe.isActive()) {
                    continue;
                }
                //左侧label处理
                left.put("label", fieldDescribe.getLabel());
                if (fieldValue == null || StringUtils.isBlank(fieldValue.toString())) {
                    continue;
                }
                //右侧label处理
                Object finalVal = ObjectDataFieldConvertUtil.me().transformDataField(user, fieldValue, fieldDescribe);
                if (finalVal != null && StringUtils.isNotBlank(finalVal.toString())) {
                    right.put("label", finalVal);
                }
            }
        }
    }
}

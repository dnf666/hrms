package com.facishare.crm.customeraccount.predefine.manager;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/3/9.
 */
@Slf4j
public class DescribeManager {

    public static IFieldDescribe getFieldDescribe(User user, String objectApiName, String fieldApiname) {
        DescribeLogicService describeLogicService = SpringUtil.getContext().getBean("describeLogicService", DescribeLogicService.class);
        IObjectDescribe objectDescribe = describeLogicService.findObject(user.getTenantId(), objectApiName);
        IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(fieldApiname);
        return fieldDescribe;
    }
}

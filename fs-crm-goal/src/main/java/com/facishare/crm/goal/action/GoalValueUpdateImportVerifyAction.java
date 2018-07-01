package com.facishare.crm.goal.action;

import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by renlb on 2018/5/7.
 */
public class GoalValueUpdateImportVerifyAction extends StandardUpdateImportVerifyAction {
    private GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext().getBean("goalValueCommonService");

    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribeList = super.getValidImportFields();
        if (CollectionUtils.notEmpty(fieldDescribeList)) {
            Map<Integer, String> fieldsMap = goalValueCommonService.getFieldsMap(1,false);
            return fieldDescribeList.stream().filter(field -> fieldsMap.values().contains(field.getApiName())).collect(Collectors.toList());
        }

        return fieldDescribeList;
    }
}

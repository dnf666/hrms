package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import java.util.List;

public class PriceBookAddAction extends StandardAddAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        validatePriceBookExpireTime(objectDescribe, Lists.newArrayList(objectData));
    }

    /**
     * 验证价目表的有效开始时间和结束时间
     */
    protected void validatePriceBookExpireTime(IObjectDescribe objectDescribe, List<IObjectData> dataList) {
        String startDateApiName = PriceBookConstants.Field.STARTDATE.getApiName();
        String endDateApiName = PriceBookConstants.Field.ENDDATE.getApiName();
        IFieldDescribe startDateFieldDescribe = objectDescribe.getFieldDescribe(startDateApiName);
        IFieldDescribe endDateFieldDescribe = objectDescribe.getFieldDescribe(endDateApiName);

        String startDateLabel = startDateFieldDescribe != null ? startDateFieldDescribe.getLabel() : PriceBookConstants.Field.STARTDATE.getLabel();
        String endDateLabel = endDateFieldDescribe != null ? endDateFieldDescribe.getLabel() : PriceBookConstants.Field.ENDDATE.getLabel();
        dataList.forEach(data -> {
            Long startDateTime = objectData.get(startDateApiName, Long.class);
            Long endDateTime = objectData.get(endDateApiName, Long.class);
            if (startDateTime != null && endDateTime != null && startDateTime > endDateTime) {
                throw new ValidateException(String.format("价目表%s不得大于%s", startDateLabel, endDateLabel));
            }
        });
    }
}

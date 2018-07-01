package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.crm.sfa.utilities.util.PriceBookSpecImpCheckGrayTenantUtil;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by luxin on 2018/4/2.
 */
public class PriceBookInsertImportDataAction extends StandardInsertImportDataAction {

    private static final String START = "{\"type\":\"hasCondition\",\"value\":{\"conditions\":[";

    private static final String END = "],\"type\":\"or\"}}";

    private static final String DEFAULT_VALUE = "{\"type\":\"noCondition\",\"value\":\"ALL\"}";

    private static final String CONDITION = "{\"type\":\"and\",\"conditions\":[{\"left\":{\"expression\":\"name\"},\"right\":{\"type\":{\"name\":\"text\"},\"value\":\"%s\"},\"type\":\"EQ\"}]}";

    @Override
    protected List<IObjectData> importData(List<IObjectData> validList) {
        String tenantId = this.getActionContext().getTenantId();

        if (PriceBookSpecImpCheckGrayTenantUtil.checkGrayTenant(tenantId)) {

            for (IObjectData objectData : validList) {
                String accountRanges = objectData.get("account_range", String.class);

                if (StringUtils.isEmpty(accountRanges)) {
                    objectData.set("account_range", DEFAULT_VALUE);
                } else {
                    String[] accountNames = accountRanges.split("；");

                    String nameTmp;

                    String finalStr = "";

                    for (String name : accountNames) {
                        nameTmp = name.trim();

                        if (!StringUtils.isEmpty(nameTmp)) {
                            finalStr += String.format(CONDITION, nameTmp) + ",";
                        }
                    }

                    if (!StringUtils.isEmpty(finalStr)) {
                        finalStr = START + finalStr.substring(0, finalStr.length() - 1) + END;
                        objectData.set("account_range", finalStr);
                    } else {
                        objectData.set("account_range", DEFAULT_VALUE);
                    }

                }
            }
        }
        return super.importData(validList);
    }

    @Override
    protected void customValidate(List<ImportData> dataList) {
        validatePriceBookExpireTime(dataList);
    }


    @Override
    protected void customDefaultValue(List<IObjectData> validList) {
        super.customDefaultValue(validList);

        String tenantId = actionContext.getTenantId();
        if (PriceBookSpecImpCheckGrayTenantUtil.checkGrayTenant(tenantId)) {
            validList.forEach(objectData -> {
                objectData.set("is_standard", "false");
                objectData.set("record_type", "default__c");
            });
        } else {
            validList.forEach(objectData -> {
                objectData.set("account_range", "{\"type\":\"noCondition\",\"value\":\"ALL\"}");
                objectData.set("is_standard", "false");
                objectData.set("record_type", "default__c");
            });
        }
    }

    /**
     * 验证价目表的有效开始时间和结束时间
     */
    private void validatePriceBookExpireTime(List<ImportData> dataList) {
        List<ImportError> errorList = Lists.newArrayList();
        String startDateApiName = PriceBookConstants.Field.STARTDATE.getApiName();
        String endDateApiName = PriceBookConstants.Field.ENDDATE.getApiName();

        IFieldDescribe startDateFieldDescribe = objectDescribe.getFieldDescribe(startDateApiName);
        IFieldDescribe endDateFieldDescribe = objectDescribe.getFieldDescribe(endDateApiName);

        String startDateLabel = startDateFieldDescribe != null ? startDateFieldDescribe.getLabel() : PriceBookConstants.Field.STARTDATE.getLabel();
        String endDateLabel = endDateFieldDescribe != null ? endDateFieldDescribe.getLabel() : PriceBookConstants.Field.ENDDATE.getLabel();
        dataList.forEach(importData -> {
            IObjectData objectData = importData.getData();
            Long startDateTime = objectData.get(startDateApiName, Long.class);
            Long endDateTime = objectData.get(endDateApiName, Long.class);
            if (startDateTime != null && endDateTime != null && startDateTime > endDateTime) {
                errorList.add(new ImportError(importData.getRowNo(),
                        String.format("价目表%s不得大于%s", startDateLabel, endDateLabel)));
            }
        });
        mergeErrorList(errorList);
    }

}

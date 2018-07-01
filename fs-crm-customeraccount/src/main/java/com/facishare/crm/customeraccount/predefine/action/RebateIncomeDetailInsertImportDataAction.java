package com.facishare.crm.customeraccount.predefine.action;

import java.util.ArrayList;
import java.util.List;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.predefine.manager.DescribeManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.util.DateUtil;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/2/1.
 */
@Slf4j
public class RebateIncomeDetailInsertImportDataAction extends StandardInsertImportDataAction {

    private RebateIncomeDetailManager rebateIncomeDetailManager;

    @Override
    protected void before(Arg arg) {
        log.debug("before RebateIncomeDetailInsertImportDataAction,for arg:{}", arg);
        super.before(arg);

    }

    /**
     * @param dataList
     */
    @Override
    protected void customValidate(List<ImportData> dataList) {
        super.customValidate(dataList);

        List<ImportError> errorList = Lists.newArrayList();
        log.debug("RebateIncomeDetailInsertImportDataAction customValidate dataList: {}", dataList);

        dataList.forEach(data -> {
            String incomeTypeValue = data.getData().get(RebateIncomeDetailConstants.Field.IncomeType.apiName, String.class);

            //income 字段的描述 描述是必填的<br>
            SelectOneFieldDescribe incomeTypeDescribe = (SelectOneFieldDescribe) DescribeManager.getFieldDescribe(actionContext.getUser(), RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.IncomeType.apiName);
            boolean isIncomeBelong = false;
            for (ISelectOption selectedOption : incomeTypeDescribe.getSelectOptions()) {
                if (selectedOption.getValue().equals(incomeTypeValue)) {
                    isIncomeBelong = true;
                    break;
                }
            }
            if (!isIncomeBelong) {
                errorList.add(new ImportError(data.getRowNo(), "收入类型不属于枚举值范围"));
            }

            String recordTypeValue = data.getData().get(SystemConstants.Field.RecordType.apiName, String.class);

            RecordTypeFieldDescribe recoreTypeDescribe = (RecordTypeFieldDescribe) DescribeManager.getFieldDescribe(actionContext.getUser(), RebateIncomeDetailConstants.API_NAME, SystemConstants.Field.RecordType.apiName);
            boolean isRecordTypeBelong = false;
            for (IRecordTypeOption recordTypeOption : recoreTypeDescribe.getRecordTypeOptions()) {
                log.debug("customValidate->recordType.getApiName:{}", recordTypeOption.getApiName());
                if (recordTypeOption.getApiName().equals(recordTypeValue)) {
                    isRecordTypeBelong = true;
                    break;
                }
            }
            if (!isRecordTypeBelong) {
                errorList.add(new ImportError(data.getRowNo(), "业务类型不正确，正确值为：" + recoreTypeDescribe.getRecordTypeOptions() + "，excel中输入的值(转换后)对应为" + recordTypeValue));
            }

        });
        mergeErrorList(errorList);
    }

    private List<String> convertIncomeTypeEnumToList() {
        List<String> incomeTypeLabelList = new ArrayList<>();
        for (RebateIncomeTypeEnum rebateIncomeTypeEnum : RebateIncomeTypeEnum.values()) {
            incomeTypeLabelList.add(rebateIncomeTypeEnum.getValue());
        }
        return incomeTypeLabelList;
    }

    @Override
    protected void customAfterImport(List<IObjectData> actualList) {
        log.debug("begin after import ,for actualList:{}", actualList);
        super.customAfterImport(actualList);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);

        for (IObjectData rebatedata : actualList) {
            try {
                rebatedata.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, 0.00);
                rebatedata.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, 0.00);

                //为null 的话不能进行Date.class强制转换
                Object startTime = rebatedata.get(RebateIncomeDetailConstants.Field.StartTime.apiName);
                Object endTime = rebatedata.get(RebateIncomeDetailConstants.Field.EndTime.apiName);
                //默认开始时间为当天
                if (startTime == null || startTime.toString().equals("0")) {
                    rebatedata.set(RebateIncomeDetailConstants.Field.StartTime.apiName, DateUtil.getNowBenginTime());
                }
                //默认结束时间为10年
                if (endTime == null || endTime.toString().equals("0")) {
                    long endTimeStr = DateUtil.getNowBenginDateTime().plusYears(10).getMillis();
                    rebatedata.set(RebateIncomeDetailConstants.Field.EndTime.apiName, endTimeStr);
                }
                rebateIncomeDetailManager.updateBalanceForLifeStatus(actionContext.getUser(), rebatedata, SystemConstants.LifeStatus.Ineffective.value, SystemConstants.LifeStatus.Normal.value);
            } catch (Exception e) {
                log.error("exception eccor when updateBalance,for rebatedata:{}", rebatedata, e);
            }
        }
    }
}

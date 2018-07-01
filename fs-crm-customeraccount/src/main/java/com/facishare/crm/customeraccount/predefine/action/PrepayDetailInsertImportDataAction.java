package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.DescribeManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/2/1.
 *
 */
@Slf4j
public class PrepayDetailInsertImportDataAction extends StandardInsertImportDataAction {

    private PrepayDetailManager prepayDetailManager;

    @Override
    protected void customDefaultValue(List<IObjectData> validList) {
        super.customDefaultValue(validList);
    }

    /**
     * 背景：只支持导入收入，不支持导入预存款支出。<br>
     * @param dataList
     */
    @Override
    protected void customValidate(List<ImportData> dataList) {
        super.customValidate(dataList);

        List<ImportError> errorList = Lists.newArrayList();
        log.debug("PrepayDetailInsertImportDataAction customValidate dataList: {}", dataList);

        dataList.forEach(data -> {
            String recordTypeValue = data.getData().get(SystemConstants.Field.RecordType.apiName, String.class);
            String incomeTypeValue = data.getData().get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);

            //income 字段的描述 描述是必填的<br>
            SelectOneFieldDescribe incomeTypeDescribe = (SelectOneFieldDescribe) DescribeManager.getFieldDescribe(actionContext.getUser(), PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.IncomeType.apiName);
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
            if (!recordTypeValue.equals(PrepayDetailConstants.RecordType.IncomeRecordType.apiName)) {
                errorList.add(new ImportError(data.getRowNo(), "业务类型不正确，业务类型只能是：收入"));
            }

        });

        mergeErrorList(errorList);

    }

    @Override
    protected void customAfterImport(List<IObjectData> actualList) {
        super.customAfterImport(actualList);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        for (IObjectData prepaydata : actualList) {
            try {
                prepayDetailManager.updateBalance(actionContext.getUser(), prepaydata, SystemConstants.LifeStatus.Ineffective.value);
            } catch (Exception e) {
                log.error("exception eccor when updateBalance,for prepaydata:{}", prepaydata);
            }
        }
    }
}

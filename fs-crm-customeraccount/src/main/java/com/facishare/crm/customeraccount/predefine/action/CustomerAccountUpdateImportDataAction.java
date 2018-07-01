package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.predefine.manager.DescribeManager;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.impl.describe.SelectManyFieldDescribe;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/2/1.
 */
@Slf4j
public class CustomerAccountUpdateImportDataAction extends StandardUpdateImportDataAction {

    /**
     * 1.信用额度只能为金额、结算方式只能为枚举。<br>
     * 2.客户账户id、客户名称、结算方式共3个字段 必填。
     */
    @Override
    protected void customValidate(List<BaseImportDataAction.ImportData> dataList) {
        super.customValidate(dataList);
        List<ImportError> errorList = Lists.newArrayList();

        dataList.forEach(data -> {
            IObjectData customerAccountObj = data.getData();
            log.info("customValidate->customerAccountObj:{}", customerAccountObj);
            customerAccountObj.get(CustomerAccountConstants.Field.CreditQuota.apiName, String.class);
            List<String> settleTypesToBeImport = (List) customerAccountObj.get(CustomerAccountConstants.Field.SettleType.apiName);
            SelectManyFieldDescribe selectManyFieldDescribe = (SelectManyFieldDescribe) DescribeManager.getFieldDescribe(actionContext.getUser(), CustomerAccountConstants.API_NAME, CustomerAccountConstants.Field.SettleType.apiName);
            boolean isSettleTypeBelong = false;
            for (String settleTypeToBeImport : settleTypesToBeImport) {
                for (ISelectOption selectOption : selectManyFieldDescribe.getSelectOptions()) {
                    log.debug("selectOptin:{},settleTypeTobeImport:{}", selectOption.getValue(), settleTypeToBeImport);
                    if (settleTypeToBeImport.equals(selectOption.getValue())) {
                        isSettleTypeBelong = true;
                    }
                }
                if (!isSettleTypeBelong) {
                    log.info("结算方式不在枚举值范围内，不在枚举值的settleType为:{},customerAccountName:{}", settleTypeToBeImport, customerAccountObj.getName());
                    errorList.add(new ImportError(data.getRowNo(), "业务类型不正确，正确值为：" + selectManyFieldDescribe.getSelectOptions()));
                    //有一个不成功就跳出，比如导入数据中settleType为{s1,s2,s3},描述里为{},一个个遍历 s1,s2,se遇到一个s失败就跳出循环。
                    break;
                }
            }
            mergeErrorList(errorList);
        });
    }

}

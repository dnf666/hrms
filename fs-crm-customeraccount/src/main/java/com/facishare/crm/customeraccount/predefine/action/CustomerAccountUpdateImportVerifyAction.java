package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.ImportConstants;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportVerifyAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by xujf on 2018/2/26.
 */
@Slf4j
public class CustomerAccountUpdateImportVerifyAction extends StandardUpdateImportVerifyAction {

    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribeList = super.getValidImportFields();

        fieldDescribeList.removeIf(f -> !ImportConstants.CUSTOMER_ACCOUNT_EXPORT_HEADER_FILTER.contains(f.getApiName()));

        log.info("getValidImportFields()->fieldDescribeList:{}", fieldDescribeList);
        return fieldDescribeList;
    }
}

package com.facishare.crm.customeraccount.predefine.action;

import java.util.Iterator;
import java.util.List;

import com.facishare.crm.customeraccount.constants.ImportConstants;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/2/1.<br>
 * 1.由于导入根据excel中的列与describe的label对应，所以导入模板列只能过滤不能修改列的label。<br>
 */
@Slf4j
public class CustomerAccountUpdateImportTemplateAction extends
        StandardUpdateImportTemplateAction{

    protected void customHeader(List<IFieldDescribe> headerFieldList) {

        Iterator<IFieldDescribe> listIter = headerFieldList.iterator();
        while (listIter.hasNext()) {
            if (!ImportConstants.CUSTOMER_ACCOUNT_EXPORT_HEADER_FILTER.contains(listIter.next().getApiName())) {
                listIter.remove();
            }
        }
        log.info("customedHeader()->:{}", listIter);

    }
}

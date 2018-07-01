package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.ImportConstants;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportTemplateAction;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

/**
 * Created by xujf on 2018/2/1.
 */
@Slf4j
public class RebateIncomeDetailInsertImportTemplateAction extends StandardInsertImportTemplateAction {

    //客户名称、金额、收入类型、业务类型为必填
    //数据列显示客户名称、金额、交易时间、收入类型、备注、业务类型等字段。不显示支出类型、附件、支付流水号、回款编号、退款编号等字段
    //private static final List<String> mustExportHeaderFilter = Lists.newArrayList(PrepayDetailConstants.Field.Amount.apiName, PrepayDetailConstants.Field.IncomeType.apiName, PrepayDetailConstants.Field.Name.apiName);

    //数据列显示客户名称、收入类型、返利金额、开始时间、结束时间、备注等字段，不显示退款、附件等字段

    protected void customHeader(List<IFieldDescribe> headerFieldList) {
        Iterator<IFieldDescribe> listIter = headerFieldList.iterator();

        while (listIter.hasNext()) {
            String fieldApiName = listIter.next().getApiName();
            log.info("customHeader->FieldDescribe:{}", fieldApiName);
            if (ImportConstants.REBATE_INCOME_MUST_NOT_EXPORT_HEADER_FILTER.contains(fieldApiName)) {
                listIter.remove();
            }
        }
    }
}

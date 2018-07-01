package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.CasesImportService;
import com.facishare.crm.sfa.predefine.service.CasesUpdateImportServiceImpl;
import com.facishare.crm.sfa.utilities.constant.CasesConstants;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportDataAction;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by luxin on 2018/5/5.
 */
public class CasesUpdateImportDataAction extends StandardUpdateImportDataAction {

    private CasesImportService casesImportService = SpringUtil
            .getContext().getBean("casesUpdateImportService", CasesUpdateImportServiceImpl.class);

    @Override
    protected Map<IFieldDescribe, List<String>> getFieldDefObjMap(ObjectDescribeExt objectDescribeExt,
                                                                  List<StandardInsertImportDataAction.ImportData> dataList) {
        Map<IFieldDescribe, List<String>> defObjMap = Maps.newHashMap();
        //组织数据，准备调用name获取id的服务
        ObjectDescribeExt describeExt = ObjectDescribeExt.of(objectDescribeExt);

        //将预制的 客户/联系人/销售订单 过滤掉
        describeExt.filter(a -> (Objects.equals(IFieldType.OBJECT_REFERENCE, a.getType()) || Objects.equals(IFieldType.MASTER_DETAIL, a.getType()))
                && !CasesConstants.REF_OBJECT_API_NAME_2_DB_KEY_WORD.values().contains(a.getApiName())).forEach(f -> {
            List<String> nameList = getNameList(dataList, f.getApiName());
            defObjMap.put(f, nameList);
        });
        return defObjMap;
    }


    @Override
    protected void customValidate(List<ImportData> importDataList) {
        super.customValidate(importDataList);
        casesImportService.setEmptyValueToUpdate(arg.getIsEmptyValueToUpdate());
        List<BaseImportAction.ImportError> errorList = casesImportService.customValidate(actionContext.getTenantId(), importDataList);

        if (CollectionUtils.notEmpty(errorList)) {
            mergeErrorList(errorList);
        }
    }

}

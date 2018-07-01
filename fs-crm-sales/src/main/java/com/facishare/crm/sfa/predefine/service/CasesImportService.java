package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luxin on 2018/5/10.
 */
public interface CasesImportService {

    /**
     * 特殊校验逻辑
     *
     * @param tenantId
     * @param importDataList
     * @return
     */
    List<BaseImportAction.ImportError> customValidate(String tenantId, List<BaseImportDataAction.ImportData> importDataList);


    /**
     * 将导入数据预处理得到校验的参数
     *
     * @param importDataList
     * @param errorList
     * @param accountNames
     * @param contactNames
     * @param salesOrderNames
     * @param rowNum2ImportData
     */
    void preprocessingValidateParam(List<BaseImportDataAction.ImportData> importDataList,
                                    List<BaseImportAction.ImportError> errorList, Set<String> accountNames,
                                    Set<String> contactNames, Set<String> salesOrderNames,
                                    Map<Integer, BaseImportDataAction.ImportData> rowNum2ImportData);

    /**
     * @return
     */
    boolean isEmptyValueToUpdate();

    void setEmptyValueToUpdate(boolean isEmptyValueToUpdate);
}

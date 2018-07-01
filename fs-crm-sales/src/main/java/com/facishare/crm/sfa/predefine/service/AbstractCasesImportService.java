package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luxin on 2018/5/10.
 * 关于销售订单的注释,如果下一期也没有销售订单对象可以将注释都删除.
 */
public abstract class AbstractCasesImportService implements CasesImportService {

    private boolean isEmptyValueToUpdate;

    @Autowired
    private PredefinedObjSearchServiceManger predefinedObjSearchServiceManger;

    @Override
    public List<BaseImportAction.ImportError> customValidate(String tenantId, List<BaseImportDataAction.ImportData> importDataList) {
        List<BaseImportAction.ImportError> errorList = Lists.newArrayList();

        Set<String> accountNames = Sets.newHashSet();
        Set<String> contactNames = Sets.newHashSet();
        //Set<String> salesOrderNames = Sets.newHashSet();

        Map<Integer, BaseImportDataAction.ImportData> rowNum2ImportData = Maps.newHashMap();
        //预处理需要校验的数据
        preprocessingValidateParam(importDataList, errorList, accountNames, contactNames, null, rowNum2ImportData);

        Map<String, String> accountName2AccountId = Maps.newHashMap();
        List<IObjectData> accountDataList = getObjectDataList(tenantId, Utils.ACCOUNT_API_NAME, accountNames);
        for (IObjectData accountData : accountDataList) {
            String name = accountData.get("name", String.class);
            String accountId = accountData.get("account_id", String.class);
            if (!StringUtils.isAnyBlank(name, accountId)) {
                accountName2AccountId.put(name, accountId);
            }
        }

        Map<String, Set<String>> contactName2AccountIds = Maps.newHashMap();
        Map<String, String> contactNameAccountIdKey2ContactId = Maps.newHashMap();
        List<IObjectData> contactDataList = getObjectDataList(tenantId, Utils.CONTACT_API_NAME, contactNames);
        for (IObjectData contactData : contactDataList) {
            String name = contactData.get("name", String.class);
            String accountId = contactData.get("account_id", String.class);

            if (!StringUtils.isAnyBlank(name, accountId)) {
                Set<String> tmpAccountIds = contactName2AccountIds.get(name);
                if (tmpAccountIds != null) {
                    tmpAccountIds.add(accountId);

                } else {
                    contactName2AccountIds.put(name, Sets.newHashSet(accountId));
                }

                String contactId = contactData.get("contact_id", String.class);
                contactNameAccountIdKey2ContactId.put(name + accountId, contactId);
            } else if (StringUtils.isNotBlank(name) && StringUtils.isBlank(accountId)) {
                contactName2AccountIds.put(name, Sets.newHashSetWithExpectedSize(0));
            }
        }

        /*Map<String, String> salesOrderName2AccountId = Maps.newHashMap();
        Map<String, String> accountId2SalesOrderId = Maps.newHashMap();
        List<IObjectData> salesOrderDataList = getObjectDataList(tenantId, Utils.SALES_ORDER_API_NAME, salesOrderNames);
        for (IObjectData salesOrderData : salesOrderDataList) {
            String name = salesOrderData.get("name", String.class);
            String accountId = salesOrderData.get("account_id", String.class);
            if (!StringUtils.isAnyBlank(name, accountId)) {
                salesOrderName2AccountId.put(name, accountId);
                accountId2SalesOrderId.put(accountId, salesOrderData.get("sales_order_id", String.class));
            }
        }*/

        rowNum2ImportData.forEach((rowNum, importData) -> {
            int row = importData.getRowNo();
            String accountName = importData.getData().get("account_id", String.class);

            String contactName = importData.getData().get("contact_id", String.class);
            String salesOrderName = importData.getData().get("sales_order_id", String.class);

            BaseImportAction.ImportError importError = null;

            String accountId = accountName2AccountId.get(accountName);

            if (StringUtils.isBlank(accountId)) {
                importError = new BaseImportAction.ImportError(row, "[客户]=" + accountName + " 不存在或已作废");
            } else {
                importData.getData().set("account_id", accountId);
            }

            if (StringUtils.isNotBlank(contactName)) {
                if (!contactName2AccountIds.keySet().contains(contactName)) {
                    if (importError != null) {
                        importError.errorMessageAppend("[联系人]=" + contactName + " 不存在或已作废");
                    } else {
                        importError = new BaseImportAction.ImportError(row, "[联系人]=" + contactName + " 不存在或已作废");
                    }
                } else {
                    if (!contactName2AccountIds.get(contactName).contains(accountId)) {
                        if (importError != null) {
                            importError.errorMessageAppend("[联系人]=" + contactName + " 不是[客户]=" + accountName + " 下的");
                        } else {
                            importError = new BaseImportAction.ImportError(row, "[联系人]=" + contactName + " 不是[客户]=" + accountName + " 下的");
                        }
                    } else {
                        importData.getData().set("contact_id", contactNameAccountIdKey2ContactId.get(contactName + accountId));
                    }
                }
            }

            /*if (StringUtils.isNotBlank(salesOrderName)) {
                String salesOrderAccountId = salesOrderName2AccountId.get(salesOrderName);
                if (StringUtils.isBlank(salesOrderAccountId)) {
                    if (importError != null) {
                        importError.errorMessageAppend("[销售订单]=" + salesOrderName + " 不存在或已作废");
                    } else {
                        importError = new BaseImportAction.ImportError(row, "[销售订单]=" + salesOrderName + " 不存在或已作废");
                    }
                } else {
                    if (!Objects.equals(salesOrderAccountId, accountId)) {
                        if (importError != null) {
                            importError.errorMessageAppend("[销售订单]=" + salesOrderName + " 不是[客户]=" + accountName + " 下的");
                        } else {
                            importError = new BaseImportAction.ImportError(row, "[销售订单]=" + salesOrderName + " 不是[客户]=" + accountName + " 下的");
                        }
                    } else {
                        importData.getData().set("sales_order_id", accountId2SalesOrderId.get(accountId));
                    }
                }
            }*/

            if (importError != null) {
                errorList.add(importError);
            }
        });
        return errorList;
    }


    @Override
    public void preprocessingValidateParam(List<BaseImportDataAction.ImportData> importDataList,
                                           List<BaseImportAction.ImportError> errorList, Set<String> accountNames,
                                           Set<String> contactNames, Set<String> salesOrderNames,
                                           Map<Integer, BaseImportDataAction.ImportData> rowNum2ImportData) {
        for (BaseImportDataAction.ImportData importData : importDataList) {
            int rowNum = importData.getRowNo();
            String accountName = importData.getData().get("account_id", String.class);

            if (StringUtils.isBlank(accountName)) {
                errorList.add(new BaseImportAction.ImportError(rowNum, "未填写[客户]"));
            } else {
                accountNames.add(accountName);
                String contactName = importData.getData().get("contact_id", String.class);
                String salesOrderName = importData.getData().get("sales_order_id", String.class);

                rowNum2ImportData.put(rowNum, importData);

                if (StringUtils.isNotBlank(contactName)) {
                    contactNames.add(contactName);
                }
              /*  if (StringUtils.isNotBlank(salesOrderName)) {
                    salesOrderNames.add(salesOrderName);
                }*/
            }
        }
    }


    @Override
    public boolean isEmptyValueToUpdate() {
        return isEmptyValueToUpdate;
    }

    @Override
    public void setEmptyValueToUpdate(boolean isEmptyValueToUpdate) {
        this.isEmptyValueToUpdate = isEmptyValueToUpdate;
    }


    public List<IObjectData> getObjectDataList(String tenantId, String apiName, Set<String> names) {
        if (CollectionUtils.isNotEmpty(names)) {
            // TODO: 2018/5/8 性能测试及优化
            return predefinedObjSearchServiceManger.getSearchService(apiName)
                    .getObjectDataListByObjectNames(tenantId, names);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }

    public List<IObjectData> getObjectDataListByIds(String tenantId, String apiName, Set<String> objectIds) {
        if (CollectionUtils.isNotEmpty(objectIds)) {
            // TODO: 2018/5/8 性能测试及优化
            return predefinedObjSearchServiceManger.getSearchService(apiName)
                    .getObjectDataListByObjectIds(tenantId, objectIds);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }


}

package com.facishare.crm.sfa.predefine.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by luxin on 2018/5/10.
 * 关于销售订单的注释,如果下一期也没有销售订单对象可以将注释都删除.
 */
@Service("casesUpdateImportService")
public class CasesUpdateImportServiceImpl extends AbstractCasesImportService {
    @Override
    public List<BaseImportAction.ImportError> customValidate(String tenantId, List<BaseImportDataAction.ImportData> importDataList) {
        if (isEmptyValueToUpdate()) {
            return super.customValidate(tenantId, importDataList);
        } else {
            // TODO: 2018/5/11 优化
            return updateImportCustomerValidate(tenantId, importDataList);
        }
    }


    List<BaseImportAction.ImportError> updateImportCustomerValidate(String tenantId, List<BaseImportDataAction.ImportData> importDataList) {
        List<BaseImportAction.ImportError> errorList = Lists.newArrayList();

        Set<String> accountNames = Sets.newHashSet();
        Set<String> contactNames = Sets.newHashSet();
        Set<String> salesOrderNames = Sets.newHashSet();

        Set<String> casesNames = Sets.newHashSet();

        // 需要在account_id填充id数据的 row->工单name
        Map<Integer, String> needFillAccountRow2CasesName = Maps.newHashMap();
        Map<Integer, String> needFillContactRow2CasesName = Maps.newHashMap();
        // Map<Integer, String> needFillSalesOrderRow2CasesName = Maps.newHashMap();

        Map<Integer, String> needFillRowNum2CasesName = Maps.newHashMap();
        Map<Integer, BaseImportDataAction.ImportData> row2ImportData = Maps.newHashMap();
        for (BaseImportDataAction.ImportData importData : importDataList) {
            int row = importData.getRowNo();
            String casesName = importData.getData().getName();

            if (StringUtils.isBlank(casesName)) {
                errorList.add(new BaseImportAction.ImportError(row, "[工单编号]未填写"));
            } else {
                // 填充客户导入数据
                fillImportData(accountNames, casesNames, needFillAccountRow2CasesName,
                        needFillRowNum2CasesName, row2ImportData, importData, row, casesName, "account_id");
                // 填充联系人导入数据
                fillImportData(contactNames, casesNames, needFillContactRow2CasesName,
                        needFillRowNum2CasesName, row2ImportData, importData, row, casesName, "contact_id");
                /*// 填充销售订单导入数据
                fillImportData(salesOrderNames, casesNames, needFillSalesOrderRow2CasesName,
                        needFillRowNum2CasesName, row2ImportData, importData, row, casesName, "sales_order_id");*/
            }
        }

        List<IObjectData> casesObjectDataList = getObjectDataList(tenantId, Utils.CASES_API_NAME, casesNames);

        Map<String, String> casesName2AccountId = Maps.newHashMap();
        Map<String, String> casesName2ContactId = Maps.newHashMap();
        // Map<String, String> casesName2SalesOrderId = Maps.newHashMap();
        for (IObjectData objectData : casesObjectDataList) {
            String casesName = objectData.getName();
            String accountId = objectData.get("account_id", String.class);
            String contactId = objectData.get("contact_id", String.class);
            // String salesOrderId = objectData.get("sales_order_id", String.class);
            fillCasesNameAndObjectIdMapping(casesName2AccountId, casesName, accountId);
            fillCasesNameAndObjectIdMapping(casesName2ContactId, casesName, contactId);
            // fillCasesNameAndObjectIdMapping(casesName2SalesOrderId, casesName, salesOrderId);
        }

        Set<String> needSearchContactIds = Sets.newHashSet(casesName2ContactId.values());
        // Set<String> needSearchSalesOrderIds = Sets.newHashSet(casesName2SalesOrderId.values());

        List<IObjectData> notUpdateContactDataList = getObjectDataListByIds(tenantId, "ContactObj", needSearchContactIds);
        Map<String, Set<String>> contactId2AccountIds = Maps.newHashMap();
        for (IObjectData objectData : notUpdateContactDataList) {
            String contactId = objectData.get("contact_id", String.class);
            Set<String> tmp = contactId2AccountIds.get(contactId);

            if (tmp == null) {
                contactId2AccountIds.put(contactId, Sets.newHashSet(objectData.get("account_id", String.class)));
            } else {
                tmp.add(objectData.get("account_id", String.class));
            }
        }

        /*List<IObjectData> notUpdateSalesOrderDataList = getObjectDataListByIds(tenantId, "SalesOrderObj", needSearchSalesOrderIds);
        Map<String, String> salesOrderId2AccountId = Maps.newHashMap();
        for (IObjectData objectData : notUpdateSalesOrderDataList) {
            String salesOrderId = objectData.get("sales_order_id", String.class);
            String accountId = objectData.get("account_id", String.class);

            salesOrderId2AccountId.put(salesOrderId, accountId);
        }*/

        List<IObjectData> accountDataList = getObjectDataList(tenantId, Utils.ACCOUNT_API_NAME, accountNames);
        Map<String, String> accountName2AccountId = Maps.newHashMap();
        for (IObjectData accountData : accountDataList) {
            String name = accountData.get("name", String.class);
            String accountId = accountData.get("account_id", String.class);
            if (!org.apache.commons.lang3.StringUtils.isAnyBlank(name, accountId)) {
                accountName2AccountId.put(name, accountId);
            }
        }

        List<IObjectData> contactDataList = getObjectDataList(tenantId, Utils.CONTACT_API_NAME, contactNames);
        Map<String, Set<String>> contactName2AccountIds = Maps.newHashMap();
        Map<String, String> contactNameAccountIdKey2ContactId = Maps.newHashMap();
        for (IObjectData contactData : contactDataList) {
            String name = contactData.get("name", String.class);
            String accountId = contactData.get("account_id", String.class);

            if (!org.apache.commons.lang3.StringUtils.isAnyBlank(name, accountId)) {
                Set<String> tmpAccountIds = contactName2AccountIds.get(name);
                if (tmpAccountIds != null) {
                    tmpAccountIds.add(accountId);

                } else {
                    contactName2AccountIds.put(name, Sets.newHashSet(accountId));
                }

                String contactId = contactData.get("contact_id", String.class);
                contactNameAccountIdKey2ContactId.put(name + accountId, contactId);
            } else if (org.apache.commons.lang3.StringUtils.isNotBlank(name) && org.apache.commons.lang3.StringUtils.isBlank(accountId)) {
                contactName2AccountIds.put(name, Sets.newHashSetWithExpectedSize(0));
            }
        }
        /*List<IObjectData> salesOrderDataList = getObjectDataList(tenantId, Utils.SALES_ORDER_API_NAME, salesOrderNames);

        Map<String, String> salesOrderName2AccountId = Maps.newHashMap();
        Map<String, String> accountId2SalesOrderId = Maps.newHashMap();
        for (IObjectData salesOrderData : salesOrderDataList) {
            String name = salesOrderData.get("name", String.class);
            String accountId = salesOrderData.get("account_id", String.class);
            if (!org.apache.commons.lang3.StringUtils.isAnyBlank(name, accountId)) {
                salesOrderName2AccountId.put(name, accountId);
                accountId2SalesOrderId.put(accountId, salesOrderData.get("sales_order_id", String.class));
            }
        }*/

        row2ImportData.forEach((rowNum, importData) -> {
            int row = importData.getRowNo();
            String casesName = importData.getData().getName();
            String accountName = importData.getData().get("account_id", String.class);
            String accountId;

            if (needFillAccountRow2CasesName.keySet().contains(row)) {
                accountId = casesName2AccountId.get(casesName);
            } else {
                accountId = accountName2AccountId.get(accountName);
            }

            BaseImportAction.ImportError importError = null;
            if (StringUtils.isBlank(accountId)) {
                importError = new BaseImportAction.ImportError(row, "[客户]不存在或已作废");
            } else {
                importData.getData().set("account_id", accountId);
            }

            boolean success = true;
            if (needFillContactRow2CasesName.keySet().contains(row)) {
                String contactId = casesName2ContactId.get(casesName);
                Set<String> tmpAccountIds = contactId2AccountIds.get(contactId);
                if (StringUtils.isBlank(accountId) || tmpAccountIds == null || !tmpAccountIds.contains(accountId)) {
                    if (importError != null) {
                        importError.errorMessageAppend("[联系人]不是[客户]下的");
                    } else {
                        importError = new BaseImportAction.ImportError(row, "[联系人]不是[客户]下的");
                    }
                    success = false;
                }
            }

            /* if (needFillSalesOrderRow2CasesName.keySet().contains(row)) {
                String salesOrderId = casesName2SalesOrderId.get(casesName);
                String tmpAccountId = salesOrderId2AccountId.get(salesOrderId);
                if (!Objects.equals(accountId, tmpAccountId)) {
                    if (importError != null) {
                        importError.errorMessageAppend(" 销售订单不是[客户]下的");
                    } else {
                        importError = new BaseImportAction.ImportError(row, "销售订单不是[客户]下的");
                    }
                    success = false;
                }
            }*/

            if (success) {
                String contactName = importData.getData().get("contact_id", String.class);
                // String salesOrderName = importData.getData().get("sales_order_id", String.class);


                if (!StringUtils.isBlank(contactName)) {
                    if (!contactName2AccountIds.keySet().contains(contactName)) {
                        if (importError != null) {
                            importError.errorMessageAppend("[联系人]不存在或已作废");
                        } else {
                            importError = new BaseImportAction.ImportError(row, "[联系人]不存在或已作废");
                        }
                    } else {
                        if (!contactName2AccountIds.get(contactName).contains(accountId)) {
                            if (importError != null) {
                                importError.errorMessageAppend("[联系人]不是[客户]下的");
                            } else {
                                importError = new BaseImportAction.ImportError(row, "[联系人]不是[客户]下的");
                            }
                        } else {
                            importData.getData().set("contact_id", contactNameAccountIdKey2ContactId.get(contactName + accountId));
                        }
                    }
                }

                /*if (!StringUtils.isBlank(salesOrderName)) {
                    String salesOrderAccountId = salesOrderName2AccountId.get(salesOrderName);
                    if (org.apache.commons.lang3.StringUtils.isBlank(salesOrderAccountId)) {
                        if (importError != null) {
                            importError.errorMessageAppend("[销售订单]不存在或已作废");
                        } else {
                            importError = new BaseImportAction.ImportError(row, "[销售订单]不存在或已作废");
                        }
                    } else {
                        if (!Objects.equals(salesOrderAccountId, accountId)) {
                            if (importError != null) {
                                importError.errorMessageAppend("[销售订单]不是[客户]下的");
                            } else {
                                importError = new BaseImportAction.ImportError(row, "[销售订单]不是[客户]下的");
                            }
                        } else {
                            importData.getData().set("sales_order_id", accountId2SalesOrderId.get(accountId));
                        }
                    }
                }*/
            }
            if (importError != null) {
                errorList.add(importError);
            }
        });
        return errorList;
    }

    private void fillImportData(Set<String> contactNames, Set<String> casesNames, Map<Integer, String> needFillContactRow2CasesName,
                                Map<Integer, String> needFillRowNum2CasesName, Map<Integer, BaseImportDataAction.ImportData> row2ImportData,
                                BaseImportDataAction.ImportData importData, int row, String casesName, String contact_id) {
        String contactName = importData.getData().get(contact_id, String.class);
        if (StringUtils.isBlank(contactName)) {
            needFillContactRow2CasesName.put(row, casesName);
            casesNames.add(casesName);

            needFillRowNum2CasesName.put(row, casesName);
        } else {
            row2ImportData.put(row, importData);
            contactNames.add(contactName);
        }
    }

    private void fillCasesNameAndObjectIdMapping(Map<String, String> casesName2AccountId, String casesName, String accountId) {
        if (StringUtils.isNotEmpty(accountId)) {
            casesName2AccountId.put(casesName, accountId);
        }
    }

}

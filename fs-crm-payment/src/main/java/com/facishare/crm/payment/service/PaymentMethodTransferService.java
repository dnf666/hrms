package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.service.dto.PaymentTransferDispatch;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.restdriver.CRMRemoteService;
import com.facishare.paas.appframework.metadata.restdriver.TenantMetadataService;
import com.facishare.paas.appframework.metadata.restdriver.dto.GetCrmTenantList;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOption;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.ResponseData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.SourceItem;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.service.ConnectionService;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServiceModule("paymentmethodtransfer")
@Component
public class PaymentMethodTransferService extends BaseTransformerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMethodTransferService.class);
  private static List<ISelectOption> DEFAULT_OPTIONS;

  static {
    DEFAULT_OPTIONS = new ArrayList<>();
    DEFAULT_OPTIONS.add(new SelectOption(new HashMap<>(ImmutableMap.of("label", "预存款", "value", "10000", "not_usable", false))));
    DEFAULT_OPTIONS.add(new SelectOption(new HashMap<>(ImmutableMap.of("label", "返利", "value", "10001", "not_usable", false))));
    DEFAULT_OPTIONS.add(new SelectOption(new HashMap<>(ImmutableMap.of("label", "预存款+返利", "value", "10002", "not_usable", false))));
  }

  @Autowired
  private PodClient podClient;
  @Autowired
  private TableSchemeService tableSchemeService;
  @Autowired
  private ConnectionService connectionService;
  @Autowired
  private CRMRemoteService crmRemoteService;
  @Autowired
  private IObjectDescribeService describeService;
  @Autowired
  private TenantMetadataService tenantMetadataService;
  @Autowired
  private ServiceFacade serviceFacade;

  @ServiceMethod("transfer")
  public boolean doTransfer(RequestData requestData) {
    transfer(requestData);
    return true;
  }

  @Override
  protected void transfer(RequestData requestData) {
    ResponseData responseData = new ResponseData();
    List<SourceData> sourceDataList = requestData.getSourceDataList();
    List<SourceItem> sourceItemList = Lists.newArrayList();
    if (CollectionUtils.isEmpty(sourceDataList)) {
      LOGGER.warn("Source data to be transferred is empty.");
      return;
    }
    LOGGER.debug("Transfer request: {}, size: {}.", requestData.getOperationJob(),
        requestData.getSourceDataList().size());
    for (SourceData sourceData : sourceDataList) {
      try {
        SourceItem sourceItem = SourceItem.builder()
            .dbUrl(podClient.getResource(sourceData.getTenantId(), PKG, MODULE, "pg"))
            .table(sourceData.getTable())
            .tenantId(sourceData.getTenantId())
            .build();
        TableSchema tableSchema = tableSchemeService
            .getTableSchema(sourceData.getTable(), connectionService.biz);
        List<Record> recordList = Lists.newArrayList();
        recordList.addAll(parseRecord(sourceData, tableSchema));
        sourceItem.setRecordList(recordList);
        sourceItemList.add(sourceItem);
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
    }
    responseData.setSourceItemList(sourceItemList);
    responseData.setOperationJob(requestData.getOperationJob());
    try {
      sendData(responseData);
    } catch (Exception e) {
      LOGGER.error("Send data error: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    List<Record> records = new ArrayList<>();
    records.add(generateRecord(sourceData, "payment_customer"));
    records.add(generateRecord(sourceData, "payment_order"));
    return records;
  }

  private Record generateRecord(SourceData data, String tableName) {
    Record record = new Record();
    record.setOpType(OpType.UPDATE);
    record.setTable(tableName);
    String id = ConverterUtil.convert2String(data.getData().get("trade_payment_id"));
    String tenantId = data.getTenantId();
    String type = ConverterUtil.convert2String(data.getData().get("payment_type"));
    String approveEmployeeId = ConverterUtil
        .convert2String(data.getData().get("finance_employee_id"));
    String actualApproveEmployeeId =
        (StringUtils.isBlank(approveEmployeeId) || "0".equals(approveEmployeeId)) ? null
            : "{" + approveEmployeeId + "}";
    record.setWhereColumnMap(ImmutableMap.of("id", new StringColumn("id", id), "tenant_id",
        new StringColumn("tenant_id", tenantId)));
    record
        .setValueColumnMap(ImmutableMap
            .of("payment_term", new StringColumn("payment_term", type), "finance_employee_id",
                new StringColumn("finance_employee_id", actualApproveEmployeeId)));
    return record;
  }

  @ServiceMethod("method")
  public PaymentTransferDispatch.Result transferPaymentMethods(PaymentTransferDispatch.Arg arg) {
    PaymentTransferDispatch.Result result = new PaymentTransferDispatch.Result();
    if (!arg.getAll() && StringUtils.isBlank(arg.getTenantIds())) {
      return result;
    }
    if (arg.getAll()) {
      int offset = arg.getOffset();
      int limit = arg.getLimit();
      ExecutorService executor = Executors.newFixedThreadPool(arg.getPoolSize());
      while (true) {
        if (offset >= arg.getMax()) {
          break;
        }
        GetCrmTenantList.Arg tenantIdListArg = new GetCrmTenantList.Arg();
        tenantIdListArg.setLimit(limit);
        tenantIdListArg.setOffset(offset);
        List<String> tenantIdList = tenantMetadataService.getCrmTenantList(tenantIdListArg);
        if (CollectionUtils.isEmpty(tenantIdList)) {
          break;
        }
        for (String tenantId : tenantIdList) {
          executor.submit(() -> {
            LOGGER.info("Payment method transferring start at tenant {}.", tenantId);
            try {
              doTransferPaymentMethods(tenantId);
            } catch (Exception ex) {
              LOGGER.error(ex.getMessage(), ex);
            }
            LOGGER.info("Payment method transferring end at tenant {}.", tenantId);
          });
        }
        offset += limit;
      }
      return result;
    } else {
      for (String tenantId : arg.getTenantIds().split(",")) {
        try {
          doTransferPaymentMethods(tenantId);
        } catch (Exception ex) {
          LOGGER.error(ex.getMessage(), ex);
        }
      }
    }
    return result;
  }

  private void doTransferPaymentMethods(String tenantId) throws MetadataServiceException {
    List<Map<String, Object>> enums = crmRemoteService
        .findEnums(tenantId, Lists.newArrayList("EnumCRMPaymentType"));
    if (CollectionUtils.isEmpty(enums)) {
      return;
    }
    IObjectDescribe describe = serviceFacade
        .findObject(tenantId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
    SelectOneFieldDescribe cf = findSelectOneField(describe,
        CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    if (null == cf) {
      return;
    }
    List<ISelectOption> updatedOptions = new ArrayList<>();
    for (Map<String, Object> optionEnum : enums) {
      String name = optionEnum.get("ItemName").toString();
      String value = optionEnum.get("ItemCode").toString();
      if (Boolean.parseBoolean(optionEnum.get("IsDeleted").toString())) {
        continue;
      }
      if (value.equals("10000") || value.equals("10001") || value.equals("10002")) {
        continue;
      }
      ISelectOption option = new SelectOption(
          new HashMap<>(ImmutableMap.of("label", name, "value", value, "not_usable", false)));
      option.set("config", generateOptionConfig());
      updatedOptions.add(option);
    }
    updatedOptions.addAll(DEFAULT_OPTIONS);
    cf.setSelectOptions(updatedOptions);
    cf.setConfig(generateFieldConfig(cf.getConfig()));
    LOGGER.info("Payment method updating at {}", tenantId);
    describeService.updateFieldDescribe(describe, Lists.newArrayList(cf));
  }

  private SelectOneFieldDescribe findSelectOneField(IObjectDescribe describe, String fieldApiName) {
    return (SelectOneFieldDescribe) ObjectDescribeExt.of(describe)
        .getFieldDescribeSilently(fieldApiName).orElse(null);
  }

  private Map<String, Object> generateOptionConfig() {
    return ImmutableMap.of("edit", 1, "remove", 1, "enable", 1);
  }

  private Map<String, Object> generateFieldConfig(Map<String, Object> config) {
    if (null == config) {
      return new HashMap<>();
    }
    if ((int) config.getOrDefault("edit", 0) == 1) {
      config.put("add", 1);
      config.put("attrs", ImmutableMap.of("options", 1));
    }
    return config;
  }
}

package com.facishare.crm.payment.service;

import com.alibaba.fastjson.JSON;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.ResponseData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.SourceItem;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.BaseColumn;
import com.fxiaoke.transfer.dto.columns.BooleanColumn;
import com.fxiaoke.transfer.dto.columns.IntegerColumn;
import com.fxiaoke.transfer.dto.columns.JSONBColumn;
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
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServiceModule("paymentnametransfer")
@Component
public class PaymentNameTransferService extends BaseTransformerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentNameTransferService.class);

  @Autowired
  private PodClient podClient;
  @Autowired
  private TableSchemeService tableSchemeService;
  @Autowired
  private ConnectionService connectionService;

  @ServiceMethod("transfer")
  public boolean doTransfer(RequestData data) {
    transfer(data);
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
    if ("mt_field".equals(sourceData.getTable())) {
      return Lists.newArrayList(generateFieldRecord(sourceData));
    } else if ("mt_ui_component".equals(sourceData.getTable())) {
      return Lists.newArrayList(generateLayoutRecord(sourceData));
    } else {
      return new ArrayList<>();
    }
  }

  private Record generateFieldRecord(SourceData sourceData) {
    Record record = new Record();
    record.setTable("mt_field");
    record.setOpType(OpType.UPDATE);
    String id = ConverterUtil.convert2String(sourceData.getData().get("field_id"));
    String tenantId = sourceData.getTenantId();
    record.setWhereColumnMap(
        ImmutableMap.of(
            "field_id", new StringColumn("field_id", id),
            "tenant_id", new StringColumn("tenant_id", tenantId)));
    Map<String, BaseColumn> values = new HashMap<>();
    values.put("type", new StringColumn("type", "text"));
    values.put("max_length", new IntegerColumn("max_length", 255));
    values.put("is_unique", new BooleanColumn("is_unique", true));
    values.put("serial_number", new IntegerColumn("serial_number", null));
    values.put("prefix", new StringColumn("prefix", null));
    values.put("postfix", new StringColumn("postfix", null));
    values.put("start_number", new IntegerColumn("start_number", null));
    record.setValueColumnMap(values);
    return record;
  }

  private Record generateLayoutRecord(SourceData sourceData) {
    Record record = new Record();
    record.setTable("mt_ui_component");
    record.setOpType(OpType.UPDATE);
    String id = ConverterUtil.convert2String(sourceData.getData().get("layout_id"));
    String tenantId = sourceData.getTenantId();
    String components = ((Map) sourceData.getData().get("components")).get("value").toString();
    Map<String, BaseColumn> whereMap = new HashMap<>();
    whereMap.put("layout_id", new StringColumn("layout_id", id));
    whereMap.put("tenant_id", new StringColumn("tenant_id", tenantId));
    record.setWhereColumnMap(whereMap);
    components = components.replace("\"field_name\": \"name\", \"is_readonly\": true",
        "\"field_name\": \"name\", \"is_readonly\": false");
    Map<String, BaseColumn> values = new HashMap<>();
    values.put("components", new JSONBColumn("components", JSON.parseArray(components)));
    record.setValueColumnMap(values);
    return record;
  }
}

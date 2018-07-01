package com.facishare.crm.payment.transfer;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.IdUtil;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFieldTransformer extends Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomFieldTransformer.class);

  private static Set<String> NO_HOLDER_TYPES = Sets.newHashSet("group");
  private ConcurrentHashMap<String, AtomicLong> paymentFieldMaxNumber = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, Long> paymentFieldNumber = new ConcurrentHashMap<>();

  public CustomFieldTransformer(
      ServiceFacade serviceFacade) {
    super(serviceFacade);
  }

  @Override
  public List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    String ei = sourceData.getTenantId();
    Map<String, Object> data = sourceData.getData();
    String fieldApiName = ConverterUtil.convert2String(data.get("api_name"));
    List<Record> records = Lists.newArrayList();
    IObjectDescribe customerPaymentDescribe = getDescribe(ei,
        PaymentObject.CUSTOMER_PAYMENT.getApiName());
    IObjectDescribe orderPaymentDescribe = getDescribe(ei,
        PaymentObject.ORDER_PAYMENT.getApiName());
    if (null != customerPaymentDescribe) {
      records.addAll(generateRecord(sourceData, tableSchema, customerPaymentDescribe, fieldApiName));
    }
    if (null != orderPaymentDescribe) {
      records.addAll(generateRecord(sourceData, tableSchema, orderPaymentDescribe, fieldApiName));
    }
    return records;
  }

  private List<Record> generateRecord(SourceData sourceData, TableSchema tableSchema,
      IObjectDescribe describe, String fieldApiName) {
    List<Record> records = new ArrayList<>();
    Record record = new Record();
    record.setTable(sourceData.getTable());
    Map<String, Object> data = sourceData.getData();
    record.setOpType(OpType.UPSERT);
    String fieldId = getFieldId(describe, fieldApiName);
    if (StringUtils.isBlank(fieldId)) {
      fieldId = IdUtil.generateId();
    }
    record.addIdColumn(new StringColumn("field_id", fieldId));
    record.addIdColumn(new StringColumn("tenant_id", sourceData.getTenantId()));
    buildRecordValueColumns(sourceData, tableSchema, record);
    String type = ConverterUtil.convert2String(data.get("type"));
    String apiName = ConverterUtil.convert2String(data.get("api_name"));
    if ("multi_level_select_one".equals(type)) {
      LOGGER.info("Parsing multi level select one field, {}", data);
    } else if ("formula".equals(type) && PaymentObject.CUSTOMER_PAYMENT.getApiName()
        .equals(describe.getApiName())) {
      String expression = ConverterUtil.convert2String(data.get("expression"));
      if (StringUtils.isNotBlank(expression) && expression.contains("order_id__r")) {
        record.setOpType(OpType.DELETE);
        record.setWhereColumnMap(record.getIdColumnMap());
      }
    }
    if (!PaymentObject.CUSTOMER_PAYMENT.getApiName().equals(describe.getApiName())) {
      record.addStringColumn("describe_id", describe.getId());
      record.addLongColumn("create_time", System.currentTimeMillis());
    }
    record.addBooleanColumn("is_extend", true);
    if (null == data.get("field_num") && shouldDispatch(type)) {
      record.addLongColumn("field_num", getFieldNum(sourceData.getTenantId(), apiName));
    }
    records.add(record);
    return records;
  }

  private IFieldDescribe getField(IObjectDescribe describe, String fieldApiName) {
    Optional<IFieldDescribe> field = ObjectDescribeExt.of(describe)
        .getFieldDescribeSilently(fieldApiName);
    return field.orElse(null);
  }

  private String getFieldId(IObjectDescribe describe, String fieldApiName) {
    IFieldDescribe field = getField(describe, fieldApiName);
    return null == field ? null : field.getId();
  }

  private boolean shouldDispatch(String type) {
    return !StringUtils.isEmpty(type) && !NO_HOLDER_TYPES.contains(type);
  }

  private Long getFieldNum(String ei, String fieldName) {
    String key = ei + "-" + fieldName;
    if (paymentFieldNumber.containsKey(key)) {
      return paymentFieldNumber.get(key);
    }
    if (paymentFieldMaxNumber.containsKey(ei)) {
      Long number = paymentFieldMaxNumber.get(ei).addAndGet(1);
      paymentFieldNumber.put(key, number);
      return number;
    }
    try {
      IObjectDescribe paymentDescribe = getDescribe(ei, "PaymentObj");
      Optional<Integer> maxFieldNumber = paymentDescribe.getFieldDescribes().stream()
          .filter(f -> "custom".equals(f.getDefineType()) && null != f.getFieldNum())
          .map(IFieldDescribe::getFieldNum).max(Integer::compare);
      long number = maxFieldNumber.map(integer -> integer + 1).orElse(1);
      paymentFieldMaxNumber.put(ei, new AtomicLong(number));
      paymentFieldNumber.put(key, number);
      return number;
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage());
      return null;
    }
  }
}

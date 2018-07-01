package com.facishare.crm.payment.service;

import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.dto.PaymentTransferDispatch;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.ResponseData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.SourceItem;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.IntegerColumn;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.utils.ConverterUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.crm.payment.constant.CrmPackageObjectConstants.FIELD_TENANT_ID;

@Component
@ServiceModule("attachmenttransfer")
@Slf4j
public class AttachmentTransferService extends BaseTransformerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentTransferService.class);
  private static final String CUSTOME_FIELD_POST = "__c";
  private static final String CREATE_TIME = "create_time";
  private static final String SPLITE_FLAG = "\\|";
  public static final String ATTACH_NAME = "attach_name";
  public static final String ATTACH_PATH = "attach_path";
  public static final String ATTACH_SIZE = "attach_size";
  public static final String FIELD_NAME = "field_name";

  public static final String TRANSFER_HOOK_PATH = "/API/v1/inner/object/attachmenttransfer/service/transfer";
  public static final String QUERY_SQL = "SELECT data_id," +
          "array_to_string(array_agg(attach_path),'|') as attach_path," +
          "array_to_string(array_agg(attach_name),'|') as attach_name," +
          "array_to_string(array_agg(create_time),'|') as create_time," +
          "array_to_string(array_agg(attach_size),'|') as attach_size," +
          "array_to_string(array_agg(field_name),'|') as field_name " +
          " from attach " +
          " WHERE source = 10 " +
          " and ei = $!{ei} " +
          " GROUP BY data_id ";

  @Autowired
  private PodClient podClient;
  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private ObjectDataServiceImpl objectDataService;
  @Autowired
  private PaymentTransferService paymentTransferService;

  private Map<String, IObjectDescribe> eiDescribeMap = Maps.newConcurrentMap();
  private final String KEY_FORMATTER = "%s|%s";

  @ServiceMethod("transfer")
  public boolean doTransfer(RequestData requestData) {

    transfer(requestData);

    return true;
  }

  @ServiceMethod("dispatch")
  public Boolean dispatch(PaymentTransferDispatch.Arg arg) {

    return paymentTransferService.createDataTransferJob(paymentTransferService.getEnv(), arg.getTenantIds(), arg.getHost(), TRANSFER_HOOK_PATH,
            QUERY_SQL);

  }

  @Override
  public void transfer(RequestData requestData) {

    ResponseData responseData = new ResponseData();
    List<SourceData> sourceDataList = requestData.getSourceDataList();

    if (CollectionUtils.isEmpty(sourceDataList)) {
      LOGGER.warn("Source data to be transferred is empty.");
    }
    List<SourceItem> sourceItemList = Lists.newArrayList();
    LOGGER.debug("Data generating at {}", System.currentTimeMillis());
    for (SourceData sourceData : sourceDataList) {

      String tableName = sourceData.getTable();
      String tenantId = sourceData.getTenantId();
      if (StringUtils.isBlank(tableName) || !tableName.equalsIgnoreCase("attach") || StringUtils.isBlank(tenantId)) {
        continue;
      }
      SourceItem sourceItem = SourceItem.builder()
              .dbUrl(podClient.getResource(tenantId, PKG, MODULE, "pg"))
              .table(tableName)
              .tenantId(tenantId)
              .build();

      List<Map<String, Object>> sourceDatas;
      try {
        sourceDatas = resolveData(sourceData);
      } catch (Exception e) {
        log.error(String.format("附件源数据异常，跳过本条数据！data [%s],msg [%s]", sourceData, e.getMessage()), e);
        continue;
      }
      sourceItem.setRecordList(generateRecordList(sourceDatas, tenantId, getDataId(sourceData.getData())));
      sourceItemList.add(sourceItem);
    }
    LOGGER.debug("Data generated at {}, data: {}", System.currentTimeMillis(), sourceItemList);
    responseData.setSourceItemList(sourceItemList);
    responseData.setOperationJob(requestData.getOperationJob());
    try {
      //发送数据给刷库中心
      sendData(responseData);
    } catch (Exception e) {
      LOGGER.error("Send data error: " + e.getMessage(), e);
    }
  }

  private List<Record> generateRecordList(List<Map<String, Object>> sourceDatas, String ei, String dataId) {

    List<Record> recordList = Lists.newArrayList();

    //预设字段以及自定义字段data_id相同,根据字段名称分组
    Map<String, List<Map<String, Object>>> fieldDataMap = sourceDatas.stream().filter(x -> !Objects.isNull(getFieldName(x)))
            .collect(Collectors.groupingBy(x -> getFieldName(x)));

    for (Map.Entry<String, List<Map<String, Object>>> entry : fieldDataMap.entrySet()) {

      String attachment = generateAttachArray(entry.getValue());
      // 附件预设对象字段为固定值
      if (entry.getKey().equalsIgnoreCase("Attach")) {//预设字段

        addUpdataRecord(recordList, dataId, attachment, "payment_customer", CustomerPaymentObj.FIELD_ATTACHMENT);
        addUpdataRecord(recordList, dataId, attachment, "payment_order", OrderPaymentObj.FIELD_ATTACHMENT);
      } else {//自定义字段

        addMtDataRecord(recordList, ei, dataId, entry.getKey(), PaymentObject.CUSTOMER_PAYMENT.getApiName(), attachment);
        addMtDataRecord(recordList, ei, dataId, entry.getKey(), PaymentObject.ORDER_PAYMENT.getApiName(), attachment);

      }
    }

    return recordList;
  }

  private void addMtDataRecord(List<Record> recordList, String ei, String dataId, String fieldName, String apiName, String attachment) {

    IObjectDescribe describe = getDescribe(ei, apiName);

    String mtDataId = getMtDataId(ei, dataId, describe);
    if (StringUtils.isBlank(mtDataId)) {
      return;
    }
    List<IObjectData> mtDatas = null;
    String sql = String.format("select * from mt_data where id = '%s'", mtDataId);
    try {
      QueryResult<IObjectData> queryResult = objectDataService.findBySql(sql,ei, apiName);
      mtDatas=queryResult.getData();
    } catch (MetadataServiceException e) {
      log.error("findBySql error. sql {},tenantId {},apiName {}",sql,ei,apiName);
    }
    String mtDataAttachColumnName = getMtDataAttachColumnName(fieldName, describe);
    if (CollectionUtils.isNotEmpty(mtDatas)) {
      addUpdataRecord(recordList, mtDataId, attachment,"mt_data", mtDataAttachColumnName);
    } else {
      addMtDataInsertRecord(recordList, mtDataId, ei, apiName, dataId, mtDataAttachColumnName, attachment);
    }
  }

  private void addMtDataInsertRecord(List<Record> recordList, String mtDataId, String ei, String apiName, String dataId, String mtDataAttachColumnName, String attachment) {

    Record record = new Record();
    record.setOpType(OpType.UPSERT);//避免多个同dataid 多个附件值需要插入
    record.setTable("mt_data");

    record.addIdColumn(new StringColumn("id", mtDataId)).addIdColumn(new StringColumn(FIELD_TENANT_ID, ei));

    record.addStringColumn(CrmPackageObjectConstants.FIELD_DESCRIBE_API_NAME, apiName);
    record.addStringColumn(CrmPackageObjectConstants.FIELD_PACKAGE, CrmPackageObjectConstants.DEFAULT_PACKAGE);
    record.addValueColumn(new IntegerColumn(CrmPackageObjectConstants.FIELD_VERSION, 1));
    record.addStringColumn(CrmPackageObjectConstants.FIELD_UDF_OBJ_ID, dataId);
    record.addStringColumn(mtDataAttachColumnName, attachment);

    recordList.add(record);
  }

  private void addUpdataRecord(List<Record> recordList, String dataId, String attachment, String payment_customer, String fieldAttachment) {
    Record record = parseRecord(payment_customer, fieldAttachment, attachment, dataId);
    if (record != null) {
      recordList.add(record);
    }
  }

  private String getMtDataAttachColumnName(String fieldName, IObjectDescribe describe) {
    IFieldDescribe field;
    try {

      field = describe.getFieldDescribe(fieldName + CUSTOME_FIELD_POST);
    } catch (Exception e) {
      return null;
    }
    if (field == null) {
      return null;
    }
    return "value" + field.getFieldNum();
  }

  private String getMtDataId(String ei, String dataId, IObjectDescribe describe) {

    IObjectData objectData = serviceFacade.findObjectData(ei, dataId, describe);
    if (objectData == null) {
      return null;
    }
    Object extendObjDataId = objectData.get(CrmPackageObjectConstants.FIELD_EXTEND_DATA_ID);
    return extendObjDataId != null ? extendObjDataId.toString() : null;
  }

  private IObjectDescribe getDescribe(String ei, String apiName) {
    String key = generateMapKey(apiName, ei);
    if (eiDescribeMap.get(key) == null) {
      IObjectDescribe describe = serviceFacade.findObject(ei, apiName);
      // 更新附件字段槽位字段,交由其他地方实现
//      updateFieldNum(recordList, describe);
      eiDescribeMap.put(key, describe);
    }
    return eiDescribeMap.get(key);
  }

  private void updateFieldNum(List<Record> recordList, IObjectDescribe describe) {
    try {
      List<IFieldDescribe> fieldDescribes = describe.getFieldDescribes();
      int maxFieldNum = fieldDescribes.stream().mapToInt(x -> x.getFieldNum()).max().orElse(1);
      for (IFieldDescribe fieldDescribe : fieldDescribes) {
        if (fieldDescribe.getDefineType().equals("custom") && fieldDescribe.getFieldNum() == null) {//自定义
          fieldDescribe.setFieldNum(maxFieldNum++);
          recordList.add(generateUpdataFieldNumRecord(fieldDescribe.getId(), fieldDescribe.getFieldNum()));
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private Record generateUpdataFieldNumRecord(String id, Integer fieldNum) {
    //需要刷草稿？
    Record record = new Record();
    record.setOpType(OpType.UPDATE);
    record.setTable("mt_field");
    record.addValueColumn(new IntegerColumn("field_num", fieldNum));
    record.addWhereColumn(new StringColumn("field_id", id));
    return record;
  }

  private String generateMapKey(String apiName, String flag) {
    return String.format(KEY_FORMATTER, apiName, flag);
  }

  private String getDataId(Map<String, Object> data) {
    return getStringValue(data, "data_id");
  }

  private String getFieldName(Map<String, Object> data) {
    return getStringValue(data, FIELD_NAME);
  }

  private String getStringValue(Map<String, Object> data, String key) {
    return ConverterUtil.convert2String(data.get(key));
  }

  private Record parseRecord(String targetTableName, String columnName, String attachment, String id) {

    if (StringUtils.isBlank(targetTableName) || StringUtils.isBlank(columnName) || StringUtils.isBlank(attachment) || StringUtils.isBlank(id)) {
      return null;
    }
    Record record = new Record();
    record.setOpType(OpType.UPDATE);
    record.setTable(targetTableName);
    record.addStringColumn(columnName, attachment);
    record.addWhereColumn(new StringColumn("id", id));
    return record;
  }

  private String generateAttachArray(List<Map<String, Object>> sourceDatas) {

    JSONArray attachmentArray = new JSONArray();
    for (Map<String, Object> data : sourceDatas) {

      JSONObject attachmentJson = new JSONObject();
      String attachName = getStringValue(data, ATTACH_NAME);
      if (StringUtils.isNotBlank(attachName)) {
        attachmentJson.put("ext", attachName.substring(attachName.lastIndexOf(".") + 1));
        attachmentJson.put("filename", attachName);
      }
      attachmentJson.put("path", getStringValue(data, ATTACH_PATH));
      attachmentJson.put(CREATE_TIME, ConverterUtil.convert2Long(data.get(CREATE_TIME)));
      attachmentJson.put("size", ConverterUtil.convert2Integer(data.get(ATTACH_SIZE)));
      attachmentArray.add(attachmentJson);
    }
    return attachmentArray.toJSONString();
  }

  @Override
  protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    return null;
  }

  private List<Map<String, Object>> resolveData(SourceData sourceData) {

    String[] createTimeArray = splitValue(sourceData, CREATE_TIME);
    String[] attachNameArray = splitValue(sourceData, ATTACH_NAME);
    String[] attachPathArray = splitValue(sourceData, ATTACH_PATH);
    String[] attachSizeArray = splitValue(sourceData, ATTACH_SIZE);
    String[] fieldNameArray = splitValue(sourceData, FIELD_NAME);

    //以下程序基于 各个数组数量应该一致，倘若数据异常，则跑出异常捕获并跳过此异常数据
    List<Map<String, Object>> sourceDatas = Lists.newArrayList();
    for (int i = 0; i < createTimeArray.length; i++) {
      Map<String, Object> map = Maps.newHashMap();

      map.put(CREATE_TIME, createTimeArray[i]);
      map.put(ATTACH_NAME, attachNameArray[i]);
      map.put(ATTACH_PATH, attachPathArray[i]);
      map.put(ATTACH_SIZE, attachSizeArray[i]);
      map.put(FIELD_NAME, fieldNameArray[i]);

      sourceDatas.add(map);
    }
    return sourceDatas;
  }

  private String[] splitValue(SourceData sourceData, String key) {
    return sourceData.getData().get(key).toString().split(SPLITE_FLAG);
  }


}

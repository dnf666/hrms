package com.facishare.crm.payment.service;

import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.IMultiLevelSelectOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.describe.MultiLevelSelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOption;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.common.Pair;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.ResponseData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.SourceItem;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.service.ConnectionService;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.fxiaoke.transfer.utils.ColumnUtil;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CascadeSelectTransferService extends BaseTransformerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CascadeSelectTransferService.class);

  @Autowired
  private PodClient podClient;
  @Autowired
  private TableSchemeService tableSchemeService;
  @Autowired
  private ConnectionService connectionService;
  @Autowired
  private ServiceFacade serviceFacade;

  private LoadingCache<String, IObjectDescribe> describeCache = CacheBuilder
      .newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(
          new CacheLoader<String, IObjectDescribe>() {
            @Override
            public IObjectDescribe load(String key) {
              return findDescribe(key);
            }
          }
      );
  private LoadingCache<String, List<IFieldDescribe>> cascadeFieldDescribeCache = CacheBuilder
      .newBuilder()
      .expireAfterWrite(1,
          TimeUnit.MINUTES).build(new CacheLoader<String, List<IFieldDescribe>>() {
        @Override
        public List<IFieldDescribe> load(String key) {
          return findCascadeSelectFields(key);
        }
      });
  private LoadingCache<String, CascadeSelectAdapter> cascadeAdapterCache = CacheBuilder
      .newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(
          new CacheLoader<String, CascadeSelectAdapter>() {
            @Override
            public CascadeSelectAdapter load(String key) {
              String[] pair = key.split("-");
              return getCascadeAdapter(pair[0], pair[1]);
            }
          }
      );

  protected abstract String getDescribeApiName();

  @Override
  protected void transfer(RequestData requestData) {
    ResponseData responseData = new ResponseData();
    List<SourceData> sourceDataList = requestData.getSourceDataList();
    List<SourceItem> sourceItemList = Lists.newArrayList();
    if (CollectionUtils.isEmpty(sourceDataList)) {
      LOGGER.warn("Source data to be transferred is empty.");
      return;
    }
    for (SourceData sourceData : sourceDataList) {
      responseData.setOperationJob(requestData.getOperationJob());
      if (hasCascadeSelectField(sourceData.getTenantId())) {
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
      }
    }
    responseData.setSourceItemList(sourceItemList);
    responseData.setSourceItemList(new ArrayList<>());
    try {
      sendData(responseData);
    } catch (Exception e) {
      LOGGER.error("Send data error: " + e.getMessage(), e);
    }
  }

  private boolean hasCascadeSelectField(String tenantId) {
    try {
      List<IFieldDescribe> cascadeFields = cascadeFieldDescribeCache.get(tenantId);
      return cascadeFields.size() > 0;
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
      return false;
    }
  }

  private IObjectDescribe findDescribe(String tenantId) {
    try {
      return serviceFacade.findObject(tenantId, getDescribeApiName());
    } catch (Exception ex) {
      return null;
    }
  }

  private List<IFieldDescribe> findCascadeSelectFields(String tenantId) {
    IObjectDescribe describe = null;
    try {
      describe = describeCache.get(tenantId);
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
    }
    if (null == describe) {
      return new ArrayList<>();
    }
    return ObjectDescribeExt.of(describe).getFieldDescribesSilently().stream()
        .filter(f -> "multi_level_select_one".equals(f.getType())).collect(Collectors.toList());
  }

  private CascadeSelectAdapter getCascadeAdapter(String tenantId, String fieldApiName) {
    IObjectDescribe describe = null;
    try {
      describe = describeCache.get(tenantId);
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
    }
    if (null == describe) {
      return null;
    }
    MultiLevelSelectOneFieldDescribe cascadeField;
    try {
      cascadeField = (MultiLevelSelectOneFieldDescribe) cascadeFieldDescribeCache
          .get(tenantId).stream()
          .filter(f -> fieldApiName.equals(f.getApiName())).findFirst().orElse(null);
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return null;
    }
    if (null == cascadeField) {
      return null;
    }
    List<IMultiLevelSelectOption> cascadeOptions = cascadeField.getSelectOptions();
    List<ISelectOption> l1Options = getLevel1Options(cascadeField.getApiName(), cascadeOptions);
    Map<String, List<ISelectOption>> l2OptionsMap = getLevel2Options(cascadeOptions);
    Map<String, String> cascadeOptionMap = new HashMap<>();
    List<ISelectOption> l2Options = new ArrayList<>();
    l2OptionsMap.forEach((key, value) -> {
      l2Options.addAll(value);
      value.forEach(o -> cascadeOptionMap.put(o.getValue(), key));
    });
    IFieldDescribe l1Field = generateSelectOneField(cascadeField.getApiName(),
        cascadeField.getLabel(), cascadeField.getDescription(), cascadeField.isRequired(),
        cascadeField.get("is_readonly", Boolean.class), l1Options);
    IFieldDescribe l2Field = generateSelectOneField(getLevel2ApiName(cascadeField.getApiName()),
        getLevel2Label(cascadeField.getLabel()), cascadeField.getDescription(), cascadeField.isRequired(),
        cascadeField.get("is_readonly", Boolean.class), l2Options);
    if (null == l1Field || null == l2Field) {
      return null;
    }
    Integer fieldNumber = getFieldNumber(describe);
    l1Field.setFieldNum(fieldNumber);
    l1Field.setIsExtend(true);
    l2Field.setFieldNum(fieldNumber + 1);
    l2Field.set("cascade_parent_api_name", l1Field.getApiName());
    l2Field.setIsExtend(true);
    describe.addFieldDescribe(l1Field);
    describe.addFieldDescribe(l2Field);
    cascadeField.setActive(false);
    cascadeField.setStatus(IFieldDescribe.STATUS_DELETED);

    serviceFacade.updateDescribe(new User(tenantId, User.SUPPER_ADMIN_USER_ID),
            describe.toJsonString() ,null,Boolean.TRUE,Boolean.FALSE);
    CascadeSelectAdapter adapter = new CascadeSelectAdapter();
    adapter.setCascadeFieldNumber(cascadeField.getFieldNum());
    adapter.setFieldNumbers(Pair.build(fieldNumber, fieldNumber + 1));
    adapter.setOptionMapping(cascadeOptionMap);
    return adapter;
  }

  private IFieldDescribe generateSelectOneField(String apiName, String label, String description,
      Boolean isRequired, Boolean isReadOnly, List<ISelectOption> options) {
    SelectOneFieldDescribe field = new SelectOneFieldDescribe();
    field.setApiName(apiName);
    field.setDefineType("custom");
    field.setLabel(label);
    field.setDescription(description);
    field.setRequired(isRequired);
    field.set("is_readonly", isReadOnly);
    field.setSelectOptions(options);
    return field;
  }

  private String getLevel2ApiName(String cascadeApiName) {
    return "L2_" + cascadeApiName;
  }

  private String getLevel2Label(String cascadeLabel) {
    return "二级" + cascadeLabel;
  }

  private List<ISelectOption> getLevel1Options(String fieldApiName,
      List<IMultiLevelSelectOption> cascadeOptions) {
    List<ISelectOption> options = new ArrayList<>();
    for (IMultiLevelSelectOption cascadeOption : cascadeOptions) {
      ISelectOption option = new SelectOption();
      option.setLabel(cascadeOption.getLabel());
      option.setValue(cascadeOption.getValue());
      option.setNotUsable(false);
      List<Map<String, List<String>>> childrenOptions = new ArrayList<>();
      Map<String, List<String>> childrenOption = new HashMap<>();
      childrenOption.put(getLevel2ApiName(fieldApiName), cascadeOption.getChildOptions().stream().map(
          IMultiLevelSelectOption::getValue).collect(Collectors.toList()));
      childrenOptions.add(childrenOption);
      option.setChildOptions(childrenOptions);
      options.add(option);
    }
    return options;
  }

  private Map<String, List<ISelectOption>> getLevel2Options(
      List<IMultiLevelSelectOption> cascadeOptions) {
    Map<String, List<ISelectOption>> result = new HashMap<>();
    for (IMultiLevelSelectOption cascadeOption : cascadeOptions) {
      List<ISelectOption> options = new ArrayList<>();
      for (IMultiLevelSelectOption cascadeChildOption : cascadeOption.getChildOptions()) {
        ISelectOption option = new SelectOption();
        option.setLabel(cascadeChildOption.getLabel());
        option.setValue(cascadeChildOption.getValue());
        option.setNotUsable(false);
        options.add(option);
      }
      result.put(cascadeOption.getValue(), options);
    }
    return result;
  }

  private Integer getFieldNumber(IObjectDescribe describe) {
    Optional<Integer> maxFieldNumber = ObjectDescribeExt.of(describe).getFieldDescribesSilently()
        .stream()
        .filter(f -> "custom".equals(f.getDefineType()) && null != f.getFieldNum())
        .map(IFieldDescribe::getFieldNum).max(Integer::compare);
    return maxFieldNumber.map(integer -> integer + 1).orElse(1);
  }

  @Override
  protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    Map<String, Object> data = sourceData.getData();
    List<IFieldDescribe> cascadeFields;
    try {
      cascadeFields = cascadeFieldDescribeCache.get(sourceData.getTenantId());
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage(), ex);
      return new ArrayList<>();
    }
    if (CollectionUtils.isEmpty(cascadeFields)) {
      return new ArrayList<>();
    }
    Record record = new Record();
    record.setTable(sourceData.getTable());
    record.setOpType(OpType.UPSERT);
    buildRecordIdColumns(sourceData, tableSchema, record);
    buildRecordValueColumns(sourceData, tableSchema, record);
    for (IFieldDescribe describe : cascadeFields) {
      CascadeSelectAdapter adapter;
      try {
        adapter = cascadeAdapterCache
            .get(sourceData.getTenantId() + "-" + describe.getApiName());
      } catch (Exception ex) {
        LOGGER.warn(ex.getMessage(), ex);
        continue;
      }
      if (null == adapter) {
        continue;
      }
      String l2Value = ConverterUtil
          .convert2String(data.get("value" + adapter.getCascadeFieldNumber()));
      String l1Value = adapter.getOptionMapping().getOrDefault(l2Value, l2Value);
      record.addStringColumn("value" + adapter.getFieldNumbers().first, l1Value);
      record.addStringColumn("value" + adapter.getFieldNumbers().second, l2Value);
    }
    return Lists.newArrayList(record);
  }

  protected void buildRecordValueColumns(SourceData sourceData, TableSchema tableSchema,
      Record record) {
    for (String sourceFieldKey : tableSchema.getColumnMap().keySet()) {
      TableSchema.Column column = tableSchema.getColumnMap().get(sourceFieldKey);
      if (!column.isPrimary()) {
        Object value = sourceData.getData().get(sourceFieldKey);
        record.addValueColumn(ColumnUtil.build(column.getName(), column.getType(), value));
      }
    }
  }

  protected void buildRecordIdColumns(SourceData sourceData, TableSchema tableSchema,
      Record record) {
    for (String sourceFieldKey : tableSchema.getColumnMap().keySet()) {
      TableSchema.Column column = tableSchema.getColumnMap().get(sourceFieldKey);
      if (column.isPrimary()) {
        Object value = sourceData.getData().get(sourceFieldKey);
        record.addIdColumn(ColumnUtil.build(column.getName(), column.getType(), value));
      }
    }
  }

  @Data
  class CascadeSelectAdapter {

    private Integer cascadeFieldNumber;
    private Pair<Integer, Integer> fieldNumbers;
    private Map<String, String> optionMapping;
  }
}

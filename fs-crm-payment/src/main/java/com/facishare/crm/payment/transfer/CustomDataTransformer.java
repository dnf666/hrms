package com.facishare.crm.payment.transfer;

import com.alibaba.fastjson.JSON;
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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class CustomDataTransformer extends Transformer {

  private LoadingCache<String, List<String>> imageFieldNames = CacheBuilder.newBuilder()
      .expireAfterWrite(1,
          TimeUnit.MINUTES).build(new CacheLoader<String, List<String>>() {
        @Override
        public List<String> load(String key) {
          String[] keyArray = key.split("-");
          return findImageFieldNames(keyArray[0], keyArray[1]);
        }
      });

  public CustomDataTransformer(ServiceFacade serviceFacade) {
    super(serviceFacade);
  }

  @Override
  public List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
    List<Record> records = Lists.newArrayList();
    IObjectDescribe describe = getDescribe(sourceData.getTenantId(),
        PaymentObject.ORDER_PAYMENT.getApiName());
    Map<String, Object> data = sourceData.getData();
    Record or = generateRecord(sourceData, tableSchema);
    String id = ConverterUtil.convert2String(data.get("id")).replace("t_pa", "pa");
    or.addIdColumn(new StringColumn("id", id));
    or.addStringColumn("object_describe_api_name", PaymentObject.ORDER_PAYMENT.getApiName());
    or.addStringColumn("object_describe_id", describe.getId());
    List<String> opImageFields = findImageFieldNamesFromCache(sourceData.getTenantId() + "-" + PaymentObject.ORDER_PAYMENT.getApiName());
    if (CollectionUtils.isNotEmpty(opImageFields)) {
      opImageFields.forEach(name -> {
        String images = ConverterUtil.convert2String(data.get(name));
        if (StringUtils.isNotBlank(images)) {
          or.addStringColumn(name, generateImageJson(images));
        }
      });
    }
    records.add(or);
    List<String> cpImageFields = findImageFieldNamesFromCache(sourceData.getTenantId() + "-" + PaymentObject.CUSTOMER_PAYMENT.getApiName());
    if (CollectionUtils.isNotEmpty(cpImageFields)) {
      Record cr = generateRecord(sourceData, tableSchema);
      cpImageFields.forEach(name -> {
        String images = ConverterUtil.convert2String(data.get(name));
        if (StringUtils.isNotBlank(images)) {
          cr.addStringColumn(name, generateImageJson(images));
        }
      });
      records.add(cr);
    }
    return records;
  }

  private List<String> findImageFieldNamesFromCache(String key) {
    try {
      return imageFieldNames.get(key);
    } catch (Exception ex) {
      return null;
    }
  }

  private Record generateRecord(SourceData sourceData, TableSchema tableSchema) {
    Record record = new Record();
    record.setTable(sourceData.getTable());
    record.setOpType(OpType.UPSERT);
    buildRecordIdColumns(sourceData, tableSchema, record);
    buildRecordValueColumns(sourceData, tableSchema, record);
    return record;
  }

  private String generateImageJson(String paths) {
    if (paths.startsWith("[")) {
      return paths;
    }
    List<Map<String, String>> transferredImage = new ArrayList<>();
    for (String path : paths.split("\\|")) {
      if (path.lastIndexOf('.') >= 0) {
        String p = path.substring(0, path.lastIndexOf("."));
        String ext = path.substring(path.lastIndexOf(".") + 1);
        transferredImage.add(ImmutableMap.of("ext", ext, "path", p));
      } else {
        transferredImage.add(ImmutableMap.of("ext", "jpg", "path", path));
      }
    }
    return JSON.toJSONString(transferredImage);
  }

  private List<IFieldDescribe> findImageFields(String tenantId, String describeApiName) {
    IObjectDescribe describe = serviceFacade.findObject(tenantId, describeApiName);
    return ObjectDescribeExt.of(describe).getFieldDescribesSilently().stream()
        .filter(f -> "image".equals(f.getType()) && "custom".equals(f.getDefineType())).collect(
            Collectors.toList());
  }

  private List<String> findImageFieldNames(String tenantId, String describeApiName) {
    List<IFieldDescribe> fields = findImageFields(tenantId, describeApiName);
    return fields.stream().map(f -> "value" + f.getFieldNum()).collect(Collectors.toList());
  }
}

package com.facishare.crm.payment.transfer;

import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.IdUtil;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.utils.ColumnUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Transformer.class);
  protected ServiceFacade serviceFacade;
  private Map<String, IObjectDescribe> describeMap = new HashMap<>();

  public Transformer(ServiceFacade serviceFacade) {
    this.serviceFacade = serviceFacade;
  }

  public abstract List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema);

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

  protected IObjectDescribe getDescribe(String ei, String apiName) {
    String key = ei + "-" + apiName;
    if (describeMap.containsKey(key)) {
      return describeMap.get(key);
    }
    try {
      IObjectDescribe describe = serviceFacade.findObject(ei, apiName);
      if (null != describe) {
        describeMap.put(ei + "-" + apiName, describe);
      }
      return describe;
    } catch (Exception ex) {
      LOGGER.warn(ex.getMessage());
      return null;
    }
  }
}

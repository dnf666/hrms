package com.facishare.crm.deliverynote.predefine.service.impl;

import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.*;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.service.ConnectionService;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class CommonTransferService extends BaseTransformerService {
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    protected TableSchemeService tableSchemeService;
    @Autowired
    protected PodClient podClient;

    private String biz = "metadata-transfer";

    @Override
    protected void transfer(RequestData requestData) {
        List<SourceData> sourceDataList = requestData.getSourceDataList();
        if (CollectionUtils.isEmpty(sourceDataList)) {
            log.warn("Source data to be transferred is empty.");
            return;
        }

        ResponseData responseData = new ResponseData();
        List<SourceItem> sourceItemList = Lists.newArrayList();
        for (SourceData sourceData : sourceDataList) {
            SourceItem sourceItem = SourceItem.builder().dbUrl(podClient.getResource(sourceData.getTenantId(), PKG, MODULE, "pg"))
                    .table(sourceData.getTable()).tenantId(sourceData.getTenantId()).build();
            TableSchema tableSchema = tableSchemeService.getTableSchema(sourceData.getTable(), connectionService.biz);
            List<Record> recordList = Lists.newArrayList();
            recordList.addAll(parseRecord(sourceData, tableSchema));
            sourceItem.setRecordList(recordList);
            sourceItemList.add(sourceItem);
        }
        responseData.setSourceItemList(sourceItemList);
        responseData.setOperationJob(requestData.getOperationJob());

        log.info("responseData====>:{}", responseData);
        //发送数据给刷库中心
        try {
            sendData(responseData);
        } catch (Exception e) {
            log.error("Send data error: " + e.getMessage(), e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.Transfer_SEND_DATA_FAILED, DeliveryNoteErrorCode.Transfer_SEND_DATA_FAILED.getMessage() + e);
        }
    }

    /**
     * onePageResultList => RequestData
     */
    protected RequestData buildRequestData(String tenantId, String tableName, List<IObjectData> onePageResultList) {
        RequestData requestData = new RequestData();
        List<SourceData> sourceDataList = Lists.newArrayList();
        Set<String> columnNames = tableSchemeService.getTableSchema(tableName, biz).getColumnNameSet();
        log.info("buildRequestData->columnNames:{}", columnNames);
        for (IObjectData objectData : onePageResultList) {
            SourceData sourceData = new SourceData();
            sourceData.setTenantId(tenantId);
            sourceData.setTable(tableName);
            Map<String, Object> data = Maps.newHashMap();
            for (String columnName : columnNames) {
                //新加的列，原来没有值 objectData.get(columnName) = null, 比如 objectData.get("delivery_money")
                data.put(columnName, objectData.get(columnName));
                // TODO: 2018/3/13 chenzs 新加的项的值，如果都有值，就不更新了
            }
            //IObjectData里Id为 "_id": "5a163dcfbab09c62bebe4d15"
            data.put("id", objectData.getId());
            sourceData.setData(data);
            sourceDataList.add(sourceData);
        }
        requestData.setSourceDataList(sourceDataList);
        return requestData;
    }

    protected Record getRecord(String id, String tenantId, String table, String columnName) {
        Record record = new Record();
        record.addUpsertColumnName(columnName);
        record.setOpType(OpType.UPSERT);
        record.setTable(table);
        record.addIdColumn(new StringColumn("id", id));                 //以前主键是ID，只要这行就可以
        record.addIdColumn(new StringColumn("tenant_id", tenantId));    //后来主键是（id+tenantId）,要加上这行
        return record;
    }

    protected Record getRecord(String tenantId, String id, String table, String columnName, BigDecimal decimalColumnValue) {
        Record record = getRecord(id, tenantId, table, columnName);
        record.addDecimalColumn(columnName, decimalColumnValue);
        return record;
    }

    protected Record getRecord(String tenantId, String id, String table, String columnName, long longColumnValue) {
        Record record = getRecord(id, tenantId, table, columnName);
//        record.addStringColumn("tenant_id", tenantId).addLongColumn(columnName, longColumnValue); //主键有了，这里不用加tenantId
        record.addLongColumn(columnName, longColumnValue);
        return record;
    }
}
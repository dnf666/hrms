package com.facishare.crm.customeraccount.service;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.*;
import com.fxiaoke.transfer.service.BaseTransformerService;
import com.fxiaoke.transfer.service.ConnectionService;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xujf on 2018/1/24.
 */
@Slf4j
public class CommonTransferService extends BaseTransformerService {
    @Autowired
    protected PodClient podClient;
    @Autowired
    protected TableSchemeService tableSchemeService;
    @Autowired
    protected ServiceFacade serviceFacade;
    @Autowired
    private ConnectionService connectionService;
    private String biz = "metadata-transfer";

    @Override
    @ServiceMethod("transfer")
    public void transfer(RequestData requestData) {
        ResponseData responseData = new ResponseData();
        List<SourceData> sourceDataList = requestData.getSourceDataList();
        List<SourceItem> sourceItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sourceDataList)) {
            log.warn("Source data to be transferred is empty.");
            return;
        }
        for (SourceData sourceData : sourceDataList) {
            SourceItem sourceItem = SourceItem.builder().dbUrl(podClient.getResource(sourceData.getTenantId(), PKG, MODULE, "pg")).table(sourceData.getTable()).tenantId(sourceData.getTenantId()).build();
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
        }
    }

    @Override
    protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
        throw new RuntimeException("not implement in this CommonTransferService");
    }

    protected void updateDescribeAndLayout(ServiceContext serviceContext) {
        throw new NotImplementedException("未实现");
    }

    protected RequestData buildRequestData(String tenantId, String tableName, List<IObjectData> onepageResultList) {
        RequestData requestData = new RequestData();
        List<SourceData> sourceDataList = Lists.newArrayList();
        Set<String> columnNames = tableSchemeService.getTableSchema(tableName, biz).getColumnNameSet();
        log.info("buildRequestData->columnNames:{}", columnNames);
        for (IObjectData objectData : onepageResultList) {
            SourceData sourceData = new SourceData();
            sourceData.setTenantId(tenantId);
            sourceData.setTable(tableName);
            Map<String, Object> data = Maps.newHashMap();
            boolean isPaymnetEmpty = false;
            for (String columnName : columnNames) {
                if (PrepayDetailConstants.Field.Payment.apiName.equals(columnName) || RebateOutcomeDetailConstants.Field.Payment.apiName.equals(columnName)) {
                    String paymentId = (String) objectData.get(columnName);
                    if (StringUtils.isEmpty(paymentId)) {
                        isPaymnetEmpty = true;
                    }
                }
                data.put(columnName, objectData.get(columnName));
            }
            if (isPaymnetEmpty) {
                continue;
            }
            //IObjectData里Id为 "_id": "5a163dcfbab09c62bebe4d15"
            data.put("id", objectData.getId());
            sourceData.setData(data);
            sourceDataList.add(sourceData);
        }
        requestData.setSourceDataList(sourceDataList);
        return requestData;
    }
}
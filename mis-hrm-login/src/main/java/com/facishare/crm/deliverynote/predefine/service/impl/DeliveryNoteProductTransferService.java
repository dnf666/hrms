package com.facishare.crm.deliverynote.predefine.service.impl;

import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.pod.client.PodClient;
import com.fxiaoke.transfer.dto.*;
import com.fxiaoke.transfer.service.TableSchemeService;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 6.3，发货单产品添加3个字段，做老数据的刷库
 *
 * Created by chenzs on 2018/3/12.
 */
@Service
@Slf4j
public class DeliveryNoteProductTransferService extends CommonTransferService {
    @Autowired
    protected TableSchemeService tableSchemeService;
    @Autowired
    protected PodClient podClient;

    /**
     * 一个发货单对一个所有的发货单产品，添加[本次发货金额、本次收货数、收货备注] 3个字段的数据
     */
    public void addData(User user, List<IObjectData> deliveryNoteProducts, String deliveryNoteStatus) {
        try {
            if (CollectionUtils.isEmpty(deliveryNoteProducts)) {
                return;
            }

            //已发货的发货单，给发货单产品添加本次收货数
            if (Objects.equals(deliveryNoteStatus, DeliveryNoteObjStatusEnum.RECEIVED.getStatus())) {
                addRealReceiveNum(deliveryNoteProducts);
            }

            RequestData requestData = buildRequestData(user.getTenantId(), DeliveryNoteProductObjConstants.STORE_TABLE_NAME, deliveryNoteProducts);
            log.info("requestData=====>{}", requestData);
            transfer(requestData);
        } catch (Exception e) {
            log.warn("error occure when transfer,for user:{}", user, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_TRANSFER_FAILED, e.getMessage());
        }
    }

    /**
     * 添加本次收货数
     */
    private void addRealReceiveNum(List<IObjectData> deliveryNoteProducts) {
        deliveryNoteProducts.forEach(deliveryNoteProduct -> {
            String deliveryNum = ConverterUtil.convert2String(deliveryNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName).toString());
            BigDecimal realReceiveNum = new BigDecimal(deliveryNum);
            deliveryNoteProduct.set(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName, realReceiveNum);
        });
    }

    @Override
    protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
        String table = DeliveryNoteProductObjConstants.STORE_TABLE_NAME;

        List<Record> records = Lists.newArrayList();
        String id = ConverterUtil.convert2String(sourceData.getData().get("id").toString());
        String tenantId = sourceData.getTenantId();
        
        //AvgPrice
        String avgPriceStr = ConverterUtil.convert2String(sourceData.getData().get(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName).toString());
        BigDecimal avgPrice = new BigDecimal(avgPriceStr);
        Record avgPriceRecord = getRecord(tenantId, id, table, DeliveryNoteProductObjConstants.Field.AvgPrice.apiName, avgPrice);
        records.add(avgPriceRecord);

        //DeliveryMoney
        String deliveryMoneyStr = ConverterUtil.convert2String(sourceData.getData().get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName).toString());
        BigDecimal deliveryMoney = new BigDecimal(deliveryMoneyStr);
        Record deliveryMoneyRecord = getRecord(tenantId, id, table, DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, deliveryMoney);
        records.add(deliveryMoneyRecord);

        //RealReceiveNum  '已收货'才有
        Object realReceiveNumObj = sourceData.getData().get(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName);
        if (realReceiveNumObj != null) {
            BigDecimal realReceiveNum = new BigDecimal(realReceiveNumObj.toString());
            Record realReceiveNumRecord = getRecord(tenantId, id, table, DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName, realReceiveNum);
            records.add(realReceiveNumRecord);
        }

        //ReceiveRemark：没数据

        return records;
    }
}
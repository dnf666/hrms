package com.facishare.crm.deliverynote.service;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.crm.deliverynote.predefine.manager.SalesOrderManager;
import com.facishare.crm.deliverynote.predefine.service.impl.DeliveryNoteTransferService;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.facishare.paas.common.util.UdobjConstants.LIFE_STATUS_API_NAME;


@Slf4j
public class DeliveryNoteTransferServiceTest {

    @Test
    public void setMoneyField_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DeliveryNoteTransferService deliveryNoteTransferService = new DeliveryNoteTransferService();
        Method method = DeliveryNoteTransferService.class.getDeclaredMethod("setMoneyField", List.class, List.class, Map.class);
        method.setAccessible(true);

        String salseOrderId = "123";

        List<IObjectData> deliveryNoteList = Lists.newArrayList();
        List<IObjectData> deliveryNoteProductList = Lists.newArrayList();
        Map<String, SalesOrderManager.OrderProduct> productId2OrderProduct = Maps.newHashMap();
        SalesOrderManager.OrderProduct orderProduct = new SalesOrderManager.OrderProduct("1", new BigDecimal("6"), new BigDecimal("20"));
        SalesOrderManager.OrderProduct orderProduct2 = new SalesOrderManager.OrderProduct("2", new BigDecimal("3"), new BigDecimal("10"));
        productId2OrderProduct.putIfAbsent(orderProduct.getProductId(), orderProduct);
        productId2OrderProduct.putIfAbsent(orderProduct2.getProductId(), orderProduct2);

        Map<String, Object> ineffectiveNoteMap = Maps.newHashMap();
        ineffectiveNoteMap.put("_id", "1");
        ineffectiveNoteMap.put(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salseOrderId);
        ineffectiveNoteMap.put(LIFE_STATUS_API_NAME, SystemConstants.LifeStatus.Ineffective.value);
        ineffectiveNoteMap.put("create_time", System.currentTimeMillis());
        IObjectData ineffectiveNote = ObjectDataExt.of(ineffectiveNoteMap);
        deliveryNoteList.add(ineffectiveNote);

        Map<String, Object> ineffectiveNoteProductMap = Maps.newHashMap();
        ineffectiveNoteProductMap.put("_id", "1");
        ineffectiveNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "1");
        ineffectiveNoteProductMap.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "1");
        ineffectiveNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("1"));
        IObjectData ineffectiveNoteProduct = ObjectDataExt.of(ineffectiveNoteProductMap);
        deliveryNoteProductList.add(ineffectiveNoteProduct);

        Map<String, Object> inValidNoteMap = Maps.newHashMap();
        inValidNoteMap.put("_id", "2");
        inValidNoteMap.put(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salseOrderId);
        inValidNoteMap.put(LIFE_STATUS_API_NAME, SystemConstants.LifeStatus.Invalid.value);
        inValidNoteMap.put("create_time", System.currentTimeMillis());
        IObjectData inValidNote = ObjectDataExt.of(inValidNoteMap);
        deliveryNoteList.add(inValidNote);

        Map<String, Object> inValidNoteProductMap = Maps.newHashMap();
        inValidNoteProductMap.put("_id", "2");
        inValidNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "2");
        inValidNoteProductMap.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "1");
        inValidNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("2"));
        IObjectData inValidNoteProduct = ObjectDataExt.of(inValidNoteProductMap);
        deliveryNoteProductList.add(inValidNoteProduct);


        Map<String, Object> normalNoteMap = Maps.newHashMap();
        normalNoteMap.put("_id", "3");
        normalNoteMap.put(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salseOrderId);
        normalNoteMap.put(LIFE_STATUS_API_NAME, SystemConstants.LifeStatus.Normal.value);
        normalNoteMap.put("create_time", System.currentTimeMillis());
        IObjectData normalNote = ObjectDataExt.of(normalNoteMap);
        deliveryNoteList.add(normalNote);

        Map<String, Object> normalNoteProductMap = Maps.newHashMap();
        normalNoteProductMap.put("_id", "3");
        normalNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "3");
        normalNoteProductMap.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "1");
        normalNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("1"));
        IObjectData normalNoteProduct = ObjectDataExt.of(normalNoteProductMap);
        deliveryNoteProductList.add(normalNoteProduct);

        Map<String, Object> normalNoteProductMap2 = Maps.newHashMap();
        normalNoteProductMap2.put("_id", "111");
        normalNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "3");
        normalNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "2");
        normalNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("1"));
        IObjectData normalNoteProduct2 = ObjectDataExt.of(normalNoteProductMap2);
        deliveryNoteProductList.add(normalNoteProduct2);


        Map<String, Object> inChangeNoteMap = Maps.newHashMap();
        inChangeNoteMap.put("_id", "4");
        inChangeNoteMap.put(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salseOrderId);
        inChangeNoteMap.put(LIFE_STATUS_API_NAME, SystemConstants.LifeStatus.InChange.value);
        inChangeNoteMap.put("create_time", System.currentTimeMillis());
        IObjectData inChangeNote = ObjectDataExt.of(inChangeNoteMap);
        deliveryNoteList.add(inChangeNote);

        Map<String, Object> inChangeNoteProductMap = Maps.newHashMap();
        inChangeNoteProductMap.put("_id", "4");
        inChangeNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "4");
        inChangeNoteProductMap.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "1");
        inChangeNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("2"));
        IObjectData inChangeNoteProduct = ObjectDataExt.of(inChangeNoteProductMap);
        deliveryNoteProductList.add(inChangeNoteProduct);
        Map<String, Object> inChangeNoteProductMap2 = Maps.newHashMap();
        inChangeNoteProductMap2.put("_id", "111");
        inChangeNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "4");
        inChangeNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "2");
        inChangeNoteProductMap2.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("2"));
        IObjectData inChangeNoteProduct2 = ObjectDataExt.of(inChangeNoteProductMap2);
        deliveryNoteProductList.add(inChangeNoteProduct2);


        Map<String, Object> underViewNoteMap = Maps.newHashMap();
        underViewNoteMap.put("_id", "5");
        underViewNoteMap.put(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salseOrderId);
        underViewNoteMap.put(LIFE_STATUS_API_NAME, SystemConstants.LifeStatus.UnderReview.value);
        underViewNoteMap.put("create_time", System.currentTimeMillis());
        IObjectData underViewNote = ObjectDataExt.of(underViewNoteMap);
        deliveryNoteList.add(underViewNote);

        Map<String, Object> underViewNoteProductMap = Maps.newHashMap();
        underViewNoteProductMap.put("_id", "5");
        underViewNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, "5");
        underViewNoteProductMap.put(DeliveryNoteProductObjConstants.Field.ProductId.apiName, "1");
        underViewNoteProductMap.put(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, new BigDecimal("3"));
        IObjectData underViewNoteProduct = ObjectDataExt.of(underViewNoteProductMap);
        deliveryNoteProductList.add(underViewNoteProduct);


        method.invoke(deliveryNoteTransferService, deliveryNoteList, deliveryNoteProductList, productId2OrderProduct);

        Assert.equals(new BigDecimal("3.33"), ineffectiveNote.get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("3.33"), ineffectiveNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));

        Assert.equals(new BigDecimal("6.66"), inValidNote.get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("6.66"), inValidNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));

        Assert.equals(new BigDecimal("6.66"), normalNote.get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("3.33"), normalNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("3.33"), normalNoteProduct2.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));

        Assert.equals(new BigDecimal("13.33"), inChangeNote.get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("6.66"), inChangeNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("6.67"), inChangeNoteProduct2.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));

        Assert.equals(new BigDecimal("10.01"), underViewNote.get(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, BigDecimal.class));
        Assert.equals(new BigDecimal("10.01"), underViewNoteProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class));
    }
}

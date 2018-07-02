package com.facishare.crm.deliverynote.manager;

import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.crm.deliverynote.predefine.manager.InitObjManager;
import com.facishare.crm.deliverynote.predefine.manager.SalesOrderManager;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteVO;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.UpdateCustomerOrderForDeliveryNoteModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.vividsolutions.jts.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class DeliveryNoteManagerTest {
    @Resource
    private DeliveryNoteManager deliveryNoteManager;
    @Resource
    private DeliveryNoteProductManager deliveryNoteProductManager;
    @Resource
    private InitObjManager initObjManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void isAllReceived_Success() {
        User user = new User("55988", "1001");
        String salesOrderId = "723444366fc3419ba10b2cd75cd0d6f1";
        boolean isAllReceived = deliveryNoteManager.isAllReceived(user, salesOrderId);
        log.info("isAllReceived {}", isAllReceived);
    }

    @Test
    public void isAllDelivered_Success() {
        User user = new User("55988", "1001");
        String salesOrderId = "fa4f5b2de1b34d458b2435bd652714da";
        boolean isAllDelivered = deliveryNoteManager.isAllDelivered(user, salesOrderId, true);
        log.info("isAllDelivered {}", isAllDelivered);
    }

    @Test
    public void getById_Success() {
        DeliveryNoteVO deliveryNoteVO = deliveryNoteManager.getById(getUser(), "5a609e34830bdbc755686cf9");
        log.info("{}", deliveryNoteVO);
    }

    @Test
    public void checkDeliveryProduct_Success() {
        String salesOrderId = "fb6916b66d9b4a0ab28f6ab408ddb506";
        String detailJsonStr = "{\"version\":\"4\",\"_id\":\"5a609e34830bdbc755686cff\",\"sales_order_id\":\"fb6916b66d9b4a0ab28f6ab408ddb506\",\"product_id\":\"df2274532dea406ab0ddc7ca9ddfc4ce\",\"specs\":\"\",\"unit\":\"块\",\"order_product_amount\":\"3.00\",\"has_delivered_num\":\"0.00\",\"stock_id\":\"5a5f2c72830bdbd7f9fd4cde\",\"real_stock\":\"1000\",\"delivery_num\":\"3\",\"object_describe_id\":\"5a6067e45f8dcc246cb53dd2\",\"object_describe_api_name\":\"DeliveryNoteProductObj\",\"record_type\":\"default__c\"}";
        IObjectData product = new Gson().fromJson(detailJsonStr, ObjectDataDocument.class).toObjectData();
        List<IObjectData> detailProductObjectDataList = Lists.newArrayList(product);
        deliveryNoteManager.checkDeliveryProduct(getUser(), salesOrderId, detailProductObjectDataList, true);
    }

    @Test
    public void getProductId2HasDeliveredAmountMap_Success() {
        User user = new User("55988", "1001");
        String salesOrderId = "91763a1ef4fc4ea2bb5084824c9e372e";
        deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(user, salesOrderId, true);
    }

    @Test
    public void checkAllDeliveryNoteIsInvalid_Success() {
        String salesOrderId = "86cbb4b448a84eb587e74e22f5c9a983";
        deliveryNoteManager.checkAllDeliveryNoteIsInvalid(getUser(), Lists.newArrayList(salesOrderId));
    }

    @Test
    public void getUpdateOrderArgProduct_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts = Lists.newArrayList();
        SalesOrderModel.SalesOrderProductVO salesOrderProductVO1 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO1.setTradeProductId("Default_1");
        salesOrderProductVO1.setProductId("1");
        salesOrderProductVO1.setAmount(new BigDecimal("1"));
        salesOrderProducts.add(salesOrderProductVO1);

        SalesOrderModel.SalesOrderProductVO salesOrderProductVO2 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO2.setTradeProductId("A_1");
        salesOrderProductVO2.setProductId("1");
        salesOrderProductVO2.setAmount(new BigDecimal("2"));
        salesOrderProducts.add(salesOrderProductVO2);

        SalesOrderModel.SalesOrderProductVO salesOrderProductVO3 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO3.setTradeProductId("Default_2");
        salesOrderProductVO3.setProductId("2");
        salesOrderProductVO3.setAmount(new BigDecimal("2"));
        salesOrderProducts.add(salesOrderProductVO3);

        SalesOrderModel.SalesOrderProductVO salesOrderProductVO4 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO4.setTradeProductId("A_2");
        salesOrderProductVO4.setProductId("2");
        salesOrderProductVO4.setAmount(new BigDecimal("2"));
        salesOrderProducts.add(salesOrderProductVO4);

        SalesOrderModel.SalesOrderProductVO salesOrderProductVO5 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO5.setTradeProductId("B_2");
        salesOrderProductVO5.setProductId("2");
        salesOrderProductVO5.setAmount(new BigDecimal("2"));
        salesOrderProducts.add(salesOrderProductVO5);

        SalesOrderModel.SalesOrderProductVO salesOrderProductVO6 = new SalesOrderModel.SalesOrderProductVO();
        salesOrderProductVO6.setTradeProductId("B_3");
        salesOrderProductVO6.setProductId("3");
        salesOrderProductVO6.setAmount(new BigDecimal("3"));
        salesOrderProducts.add(salesOrderProductVO6);

        Map<String, BigDecimal> productId2HasDeliveredAmount = Maps.newHashMap();
        productId2HasDeliveredAmount.put("1", new BigDecimal("3"));
        productId2HasDeliveredAmount.put("2", new BigDecimal("5"));
        Map<String, SalesOrderManager.OrderProduct> orderProductMap = Maps.newHashMap();
        SalesOrderManager.OrderProduct orderProduct1 = new SalesOrderManager.OrderProduct();
        orderProduct1.setProductId("1");
        orderProduct1.setAllAmount(new BigDecimal("3"));
        orderProduct1.setAllSubTotal(new BigDecimal("10"));
        orderProductMap.put(orderProduct1.getProductId(), orderProduct1);

        SalesOrderManager.OrderProduct orderProduct2 = new SalesOrderManager.OrderProduct();
        orderProduct2.setProductId("2");
        orderProduct2.setAllAmount(new BigDecimal("6"));
        orderProduct2.setAllSubTotal(new BigDecimal("20"));
        orderProductMap.put(orderProduct2.getProductId(), orderProduct2);

        SalesOrderManager.OrderProduct orderProduct3 = new SalesOrderManager.OrderProduct();
        orderProduct3.setProductId("3");
        orderProduct3.setAllAmount(new BigDecimal("3"));
        orderProduct3.setAllSubTotal(new BigDecimal("3"));
        orderProductMap.put(orderProduct3.getProductId(), orderProduct3);

        DeliveryNoteManager manager = new DeliveryNoteManager();
        Class[] parameterTypes = {List.class, Map.class, Map.class};
        Method method = DeliveryNoteManager.class.getDeclaredMethod("getUpdateOrderArgProduct", parameterTypes);
        method.setAccessible(true);
        List<UpdateCustomerOrderForDeliveryNoteModel.Arg.Product> argProducts =
                (List<UpdateCustomerOrderForDeliveryNoteModel.Arg.Product>) method.invoke(manager, salesOrderProducts, productId2HasDeliveredAmount, orderProductMap);
        log.info("{}", argProducts);

        Assert.equals(6, argProducts.size());
        argProducts.forEach(argProduct -> {
            if (argProduct.getTradeProductID().equals("Default_1")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(new BigDecimal("3.33")) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(new BigDecimal("1")) == 0);
            }

            if (argProduct.getTradeProductID().equals("A_1")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(new BigDecimal("6.67")) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(new BigDecimal("2")) == 0);
            }

            if (argProduct.getTradeProductID().equals("Default_2")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(new BigDecimal("6.66")) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(new BigDecimal("2")) == 0);
            }

            if (argProduct.getTradeProductID().equals("A_2")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(new BigDecimal("6.66")) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(new BigDecimal("2")) == 0);
            }

            if (argProduct.getTradeProductID().equals("B_2")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(new BigDecimal("3.33")) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(new BigDecimal("1")) == 0);
            }

            if (argProduct.getTradeProductID().equals("B_3")) {
                Assert.isTrue(argProduct.getDeliveryAmount().compareTo(BigDecimal.ZERO) == 0);
                Assert.isTrue(argProduct.getDeliveredCount().compareTo(BigDecimal.ZERO) == 0);
            }
        });
    }


    private User getUser() {
        return new User("69634", "1000");
    }
}
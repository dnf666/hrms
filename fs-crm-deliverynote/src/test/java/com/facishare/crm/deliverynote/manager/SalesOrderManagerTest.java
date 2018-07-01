package com.facishare.crm.deliverynote.manager;

import com.facishare.crm.deliverynote.base.BaseTest;
import com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum;
import com.facishare.crm.deliverynote.predefine.manager.SalesOrderManager;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.UpdateCustomerOrderForDeliveryNoteModel;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class SalesOrderManagerTest extends BaseTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private SalesOrderManager salesOrderManager;

    @Test
    public void getById_Success() {
        String salesOrderId = "525c4cd290fb4f4698999fecf4de503c";
        SalesOrderModel.SalesOrderVo salesOrderVo = salesOrderManager.getById(user, salesOrderId);
    }

    @Test
    public void getProductsById_Success() {
        User user = new User("55983", "1000");
        String salesOrderId = "ccd575a8aeb147e9b7fd3a8970790c49";
        List<SalesOrderModel.SalesOrderProductVO> productVoList = salesOrderManager.getSalesOrderProducts(user, salesOrderId);
        log.info("{}", productVoList);
    }

    @Test
    public void getOrderProduct_Success() {
        User user = new User("55988", User.SUPPER_ADMIN_USER_ID);
        String salesOrderId = "812a553138b148f8bb44df8e179f17f4";
        Map<String, SalesOrderManager.OrderProduct> orderProductMap = salesOrderManager.getOrderProduct(user, salesOrderId);
        Assert.equals(2, orderProductMap.size());

        SalesOrderManager.OrderProduct shrimp = orderProductMap.get("f87e2400793c4a18b90816007abea82a");
        Assert.equals(shrimp.getAllAmount(), new BigDecimal("6.00"));
        Assert.equals(shrimp.getAllSubTotal(), new BigDecimal("46.00"));
        Assert.equals(shrimp.avgPrice(), new BigDecimal("7.67"));

        SalesOrderManager.OrderProduct iphone = orderProductMap.get("16d788d315dc496f80f547d34d891acc");
        Assert.equals(iphone.getAllAmount(), new BigDecimal("3.00"));
        Assert.equals(iphone.getAllSubTotal(), new BigDecimal("15000.00"));
        Assert.equals(iphone.avgPrice(), new BigDecimal("5000.00"));

        user = new User("55424", User.SUPPER_ADMIN_USER_ID);
        salesOrderId = "30c81eb7ae014fd5aeb10d81e2b35c09";
        orderProductMap = salesOrderManager.getOrderProduct(user, salesOrderId);
        Assert.equals(2, orderProductMap.size());

        SalesOrderManager.OrderProduct glass = orderProductMap.get("22a35d5441dc45f3acb40d9be84346a6");
        Assert.equals(glass.getAllAmount(), new BigDecimal("5.00"));
        Assert.equals(glass.getAllSubTotal(), new BigDecimal("5.00"));
        Assert.equals(glass.avgPrice(), new BigDecimal("1.00"));

        SalesOrderManager.OrderProduct cap = orderProductMap.get("46f72de7f20e4c709c3cd8fa606e6868");
        Assert.equals(cap.getAllAmount(), new BigDecimal("5.00"));
        Assert.equals(cap.getAllSubTotal(), new BigDecimal("5.00"));
        Assert.equals(cap.avgPrice(), new BigDecimal("1.00"));
    }

    @Test
    public void saveModifyLog_Success() {
        User user = new User("55985", User.SUPPER_ADMIN_USER_ID);
        String salesOrderId = "47fed06e41534a7093390a33178f0262";
        String logText = "David创建的发货单已确认，编号：DN20180228-001，发货状态变更为部分发货";
        salesOrderManager.saveModifyLog(user, salesOrderId, logText);
    }

    @Test
    public void updateForDeliveryStatusChange_Success() {
        User user = new User("55985", User.SUPPER_ADMIN_USER_ID);
        String salesOrderId = "47fed06e41534a7093390a33178f0262";
    }

    @Test
    public void updateCustomerOrderForDeliveryNote_Success() {
        User user = new User("55983", User.SUPPER_ADMIN_USER_ID);
        UpdateCustomerOrderForDeliveryNoteModel.Arg arg = new  UpdateCustomerOrderForDeliveryNoteModel.Arg();
        String salesOrderId = "b7d1f98540c6457c8128f2c14109b5c2";
        arg.setCustomerTradeId(salesOrderId);
        arg.setDeliveredAmountSum(new BigDecimal("100.00"));
        arg.setConfirmReceiveTime(System.currentTimeMillis());
        arg.setConfirmDeliveryTime(System.currentTimeMillis() + 1000);
        arg.setLogisticsStatus(SalesOrderLogisticsStatusEnum.PartialDelivery.getStatus());

        String tradeProductId = "41993059eb934846b3a84ae2b386e8aa";
        UpdateCustomerOrderForDeliveryNoteModel.Arg.Product product = new UpdateCustomerOrderForDeliveryNoteModel.Arg.Product();
        product.setTradeProductID(tradeProductId);
        product.setDeliveredCount(new BigDecimal("200.00"));
        product.setDeliveryAmount(new BigDecimal("500.11"));

        arg.setUpdateDetailList(Lists.newArrayList(product));

        salesOrderManager.updateCustomerOrderForDeliveryNote(user, arg);

    }

}

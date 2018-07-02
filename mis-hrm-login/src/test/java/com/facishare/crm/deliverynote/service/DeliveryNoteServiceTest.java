package com.facishare.crm.deliverynote.service;

import com.facishare.crm.deliverynote.base.BaseServiceTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.predefine.service.DeliveryNoteService;
import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType;
import com.facishare.crm.deliverynote.predefine.service.dto.EmptyResult;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class DeliveryNoteServiceTest extends BaseServiceTest {
    @Autowired
    private DeliveryNoteService deliveryNoteService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;

    public DeliveryNoteServiceTest() {
        super(DeliveryNoteObjConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Override
    public void initUser() {
        this.tenantId = "55985";
        this.fsUserId = "1000";
    }

    @Test
    public void deleteUserDefinedActionCodeTest() {
        User user = new User("69634", "1000");
        functionPrivilegeService.deleteUserDefinedActionCode(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Button.ViewLogistics.apiName);
        functionPrivilegeService.deleteUserDefinedActionCode(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName);
        System.out.println();
    }

    @Test
    public void initPrivilegeTest() {
        DeliveryNoteType.EnableDeliveryNoteResult result = deliveryNoteService.initPrivilege(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void testUpdateSalesOrderStatusTest() {
        DeliveryNoteType.EnableDeliveryNoteResult result = deliveryNoteService.testUpdateSalesOrderStatus(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void initPrintTemplateTest() {
        DeliveryNoteType.EnableDeliveryNoteResult result = deliveryNoteService.initPrintTemplate(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void enableDeliveryNoteTest() {
        DeliveryNoteType.EnableDeliveryNoteResult result = deliveryNoteService.enableDeliveryNote(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void addFieldTest() {
        DeliveryNoteType.AddFieldResult result = deliveryNoteService.addField(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void getWarehouseIdBySalesOrderId_Success() {
        DeliveryNoteType.GetWarehouseBySalesOrderIdModel.Arg arg = new DeliveryNoteType.GetWarehouseBySalesOrderIdModel.Arg();
        arg.setSalesOrderId("fb6916b66d9b4a0ab28f6ab408ddb506");
//        arg.setSalesOrderId("6d0fbf685f3a4c15888693853d9e1c68");

        DeliveryNoteType.GetWarehouseBySalesOrderIdModel.Result result = deliveryNoteService.getWarehouseBySalesOrderId(newServiceContext(), arg);
        System.out.println(result);
    }

    @Test
    public void getCanDeliverProducts_Success() {
        DeliveryNoteType.GetCanDeliverProductsModel.Arg arg = new DeliveryNoteType.GetCanDeliverProductsModel.Arg();
        arg.setSalesOrderId("fb6916b66d9b4a0ab28f6ab408ddb506");
        arg.setWarehouseId("5a5c69f6830bdb232000b3a4");

        DeliveryNoteType.GetCanDeliverProductsModel.Result result = deliveryNoteService.getCanDeliverProducts(newServiceContext(), arg);
        log.info("result {}", result);
    }

    @Test
    public void confirmReceive_Success() {
        DeliveryNoteType.ConfirmReceiveArg arg = new DeliveryNoteType.ConfirmReceiveArg();
        arg.setDeliveryNoteId("5ac34a31bab09cf9ae1e7c6e");
        arg.setReceiveRemark("有一个损坏了");
        List<DeliveryNoteType.DeliveryNoteProduct> products = Lists.newArrayList();
        DeliveryNoteType.DeliveryNoteProduct product = new DeliveryNoteType.DeliveryNoteProduct();
        product.setProductId("f091909e263e4806bfad0cd727dceca9");
        product.setRealReceiveNum(new BigDecimal("1"));
        product.setReceiveRemark("asdfasdf");
        products.add(product);
        arg.setDeliveryNoteProducts(products);
        EmptyResult confirmReceiveResult = deliveryNoteService.confirmReceive(newServiceContext(), arg);
        log.info("confirmReceiveResult {}", confirmReceiveResult);
    }

    @Test
    public void getByDeliveryNoteId_Success() {
        DeliveryNoteType.GetByDeliveryNoteIdArg arg = new DeliveryNoteType.GetByDeliveryNoteIdArg();
        arg.setDeliveryNoteId("5a5f4ed3830bdbf39c95fafe");
        DeliveryNoteType.GetByDeliveryNoteIdResult result = deliveryNoteService.getByDeliveryNoteId(newServiceContext(), arg);
        log.info("GetByDeliveryNoteIdResult result: {}", result);
    }

    @Test
    public void getBySalesOrderId_Success() {
        DeliveryNoteType.GetBySalesOrderIdArg arg = new DeliveryNoteType.GetBySalesOrderIdArg();
        arg.setSalesOrderId("fb6916b66d9b4a0ab28f6ab408ddb506");
        DeliveryNoteType.GetBySalesOrderIdResult result = deliveryNoteService.getBySalesOrderId(newServiceContext(), arg);
        log.info("GetByDeliveryNoteIdResult result: {}" + result);
    }

    @Test
    public void getLogistics_Success() {
        DeliveryNoteType.GetLogisticsArg arg = new DeliveryNoteType.GetLogisticsArg();
        arg.setDeliveryNoteId("5a6ac5f4830bdb6d03aa3caa");
        DeliveryNoteType.GetLogisticsResult result = deliveryNoteService.getLogistics(newServiceContext(), arg);
        log.info("DeliveryNoteType.GetLogisticsResult {}", result);

    }
}
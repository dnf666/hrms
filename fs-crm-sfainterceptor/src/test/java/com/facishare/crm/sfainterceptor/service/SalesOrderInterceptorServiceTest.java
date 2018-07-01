package com.facishare.crm.sfainterceptor.service;

import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.sfainterceptor.base.BaseServiceTest;
import com.facishare.crm.sfainterceptor.predefine.service.SalesOrderInterceptorService;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddAfterModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add.SalesOrderAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd.SalesOrderBulkAddBeforeModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditBeforeModel;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linchf on 2018/1/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class SalesOrderInterceptorServiceTest extends BaseServiceTest {
    @Autowired
    private SalesOrderInterceptorService salesOrderInterceptorService;


    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public SalesOrderInterceptorServiceTest() {

            super("SalesOrderInterceptorService");

    }

    @Test
    public void bulkAddBeforeTest() throws MetadataServiceException {
        SalesOrderBulkAddBeforeModel.Arg arg = new SalesOrderBulkAddBeforeModel.Arg();
        arg.setIsCheckSalesOrder(true);
        arg.setIsCheckSalesOrderProduct(false);
        List<SalesOrderBulkAddBeforeModel.MixtureVo> mixtureVos = Lists.newArrayList();
        for (int i = 0; i < 5; i++ ) {
            SalesOrderBulkAddBeforeModel.MixtureVo mixtureVo = new SalesOrderBulkAddBeforeModel.MixtureVo();
            mixtureVo.setId("1");
            mixtureVo.setTradeProductId("TradeProductId");
            mixtureVo.setAmount(new BigDecimal(3));
            mixtureVo.setCustomerId("e832eccbfe4b4a069d5197165fd6bd49");
            mixtureVo.setProductName("productName");
            mixtureVo.setWarehouseName("深圳" + i + "仓");
            mixtureVos.add(mixtureVo);
        }

        arg.setMixtureVos(mixtureVos);
        salesOrderInterceptorService.bulkAddBefore(newServiceContext(), arg);
    }


    @Test
    public void addAfterTest() {
        SalesOrderAddAfterModel.Arg arg = new SalesOrderAddAfterModel.Arg();
        arg.setBeforeLifeStatus("ineffective");
        arg.setAfterLifeStatus("under_review");
        arg.setDataId("f94e2c349fda4ce588a55e2ff9efe2df");
        salesOrderInterceptorService.addAfter(newServiceContext(), arg);
    }


    @Test
    public void editBeforeTest() {
        SalesOrderEditBeforeModel.Arg arg = new SalesOrderEditBeforeModel.Arg();
        SalesOrderEditBeforeModel.SalesOrderVo salesOrderVo = new SalesOrderEditBeforeModel.SalesOrderVo();
        salesOrderVo.setCustomerId("27e85cb7c9264843bc1f7dc109a76a16");
        salesOrderVo.setWarehouseId("5af006c7a5083d38e24778e3");

        List<SalesOrderProductVo> salesOrderProductVos = Lists.newArrayList();
        SalesOrderProductVo salesOrderProductVo1 = new SalesOrderProductVo();
        salesOrderProductVo1.setAmount(new BigDecimal("79.00"));
        salesOrderProductVo1.setRecordType("default__c");
        salesOrderProductVo1.setProductId("e74ff7ae94644fc7bf7406e23b0ce3c5");
        salesOrderProductVo1.setPrice(new BigDecimal("2.00"));

        SalesOrderProductVo salesOrderProductVo2 = new SalesOrderProductVo();
        salesOrderProductVo2.setAmount(new BigDecimal("58.00"));
        salesOrderProductVo2.setRecordType("default__c");
        salesOrderProductVo2.setProductId("6caf1704aae74f4caf3825af5acfce4d");
        salesOrderProductVo2.setPrice(new BigDecimal("2.00"));

        SalesOrderProductVo salesOrderProductVo3 = new SalesOrderProductVo();
        salesOrderProductVo3.setAmount(new BigDecimal("5.00"));
        salesOrderProductVo3.setRecordType("record_owtI1__c");
        salesOrderProductVo3.setProductId("132901a141aa45c7b4d89de6a91297ab");
        salesOrderProductVo3.setPrice(new BigDecimal("12.00"));

        SalesOrderProductVo salesOrderProductVo4 = new SalesOrderProductVo();
        salesOrderProductVo4.setAmount(new BigDecimal("3.00"));
        salesOrderProductVo4.setRecordType("record_owtI1__c");
        salesOrderProductVo4.setProductId("e74ff7ae94644fc7bf7406e23b0ce3c5");
        salesOrderProductVo4.setPrice(new BigDecimal("2888.00"));


        salesOrderVo.setSalesOrderProductVos(Arrays.asList(salesOrderProductVo1, salesOrderProductVo2, salesOrderProductVo3, salesOrderProductVo4));
        arg.setSalesOrderVo(salesOrderVo);
        arg.setNowLifeStatus("normal");

        salesOrderInterceptorService.editBefore(newServiceContext(), arg);
    }

    @Test
    public void addBeforeTest() {
        SalesOrderAddBeforeModel.Arg arg = new SalesOrderAddBeforeModel.Arg();
        SalesOrderAddBeforeModel.SalesOrderVo salesOrderVo = new SalesOrderAddBeforeModel.SalesOrderVo();
        salesOrderVo.setCustomerId("5c5c22cacab94f4ab9f45239ae0dc88e");

        SalesOrderProductVo salesOrderProductVo1 = new SalesOrderProductVo();
        salesOrderProductVo1.setProductId("4828816f22d64e878ac1697972c08b2e");
        salesOrderProductVo1.setAmount(new BigDecimal("7.01"));
        salesOrderProductVo1.setRecordType("default__c");

        SalesOrderProductVo salesOrderProductVo2 = new SalesOrderProductVo();
        salesOrderProductVo2.setProductId("5cf08e4a55384d6ca2f43da59519a3dd");
        salesOrderProductVo2.setAmount(new BigDecimal("5.0"));
        salesOrderProductVo2.setRecordType("default__c");




        salesOrderVo.setSalesOrderProductVos(Arrays.asList(salesOrderProductVo1, salesOrderProductVo2));
        arg.setSalesOrderVo(salesOrderVo);

        salesOrderInterceptorService.addBefore(newServiceContext(), arg);
    }
}

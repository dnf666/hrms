package com.facishare.crm.sfainterceptor.service;

import com.facishare.crm.sfainterceptor.base.BaseServiceTest;
import com.facishare.crm.sfainterceptor.predefine.service.ReturnedGoodsInvoiceInterceptorService;
import com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd.ReturnedGoodsInvoiceBulkAddBeforeModel;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by linchf on 2018/1/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ReturnedGoodsInvoiceInterceptorServiceTest extends BaseServiceTest {
    @Autowired
    private ReturnedGoodsInvoiceInterceptorService returnedGoodsInvoiceInterceptorService;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public ReturnedGoodsInvoiceInterceptorServiceTest() {
        super("ReturnedGoodsInvoiceInterceptorService");
    }

    @Test
    public void bulkAddBeforeTest() {
        ReturnedGoodsInvoiceBulkAddBeforeModel.Arg arg = new ReturnedGoodsInvoiceBulkAddBeforeModel.Arg();
        arg.setIsCheckProduct(false);
        arg.setIsCheckReturnedGoodsInvoice(true);
        List<ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureVo> mixtureVos = Lists.newArrayList();
        for (int i = 0; i < 5; i++ ) {
            ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureVo mixtureVo = new ReturnedGoodsInvoiceBulkAddBeforeModel.MixtureVo();
            mixtureVo.setId("1");
            mixtureVo.setTradeId("TradeId");
            mixtureVo.setDataId("dataId");
            mixtureVo.setProductId("ProductId");
            mixtureVo.setAmount(new BigDecimal(3));
            mixtureVo.setCustomerId("e832eccbfe4b4a069d5197165fd6bd49");
            mixtureVo.setProductName("productName");
            mixtureVo.setWarehouseName("深圳" + i + "仓");
            mixtureVos.add(mixtureVo);
        }
        arg.setMixtureVos(mixtureVos);
        returnedGoodsInvoiceInterceptorService.bulkAddBefore(newServiceContext(), arg);
    }
}

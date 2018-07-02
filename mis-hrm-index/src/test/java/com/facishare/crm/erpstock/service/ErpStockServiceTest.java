package com.facishare.crm.erpstock.service;

import com.facishare.crm.erpstock.base.BaseServiceTest;
import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.predefine.service.ErpStockBizService;
import com.facishare.crm.erpstock.predefine.service.dto.ErpStockType;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ErpStockServiceTest extends BaseServiceTest {
    @Autowired
    private ErpStockBizService erpStockBizService;

    public ErpStockServiceTest() {
        super(ErpStockConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void enableStockTest() throws MetadataServiceException {
        ErpStockType.EnableErpStockResult enableResult = erpStockBizService.enableErpStock(newServiceContext());
        System.out.println(enableResult);
    }

}

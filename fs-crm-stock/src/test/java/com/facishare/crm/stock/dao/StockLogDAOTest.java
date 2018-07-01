package com.facishare.crm.stock.dao;

import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author linchf
 * @date 2018/3/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockLogDAOTest {
    @Resource
    private StockLogDAO stockLogDAO;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testBulkSave() {
        IObjectData stock = new ObjectData();

        stock.set(StockConstants.Field.Warehouse.apiName, "123");
        stock.set(StockConstants.Field.Product.apiName, "456");
        stock.set(StockConstants.Field.RealStock.apiName, "12");
        stock.set(StockConstants.Field.BlockedStock.apiName, "12");

        StockOperateInfo info = StockOperateInfo.builder()
                .operateObjectId("123")
                .operateObjectType(1)
                .operateType(1)
                .operateResult(1)
                .build();
        StockLogDO stockLogDO = StockLogDO.buildLog(new User("55985", "1000"), stock, info);
        stockLogDAO.bulkSave(Arrays.asList(stockLogDO));
    }



}

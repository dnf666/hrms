package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.predefine.manager.StockConfigManager;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by linchf on 2018/1/19.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockConfigManagerTest extends BaseTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private StockConfigManager stockConfigManager;

    @Test
    public void insertOrUpdateStockSwitch() {
        stockConfigManager.insertOrUpdateStockSwitch(new User("55988", "1000"), StockType.StockSwitchEnum.UNABLE, false);
    }

    @Test
    public void testGetStockSwitch() {
        stockConfigManager.getStockSwitch("55985");
    }

    @Test
    public void testGetOrderCheckType() {
        stockConfigManager.getOrderCheckType(new User("55985", "1000"));
    }
}

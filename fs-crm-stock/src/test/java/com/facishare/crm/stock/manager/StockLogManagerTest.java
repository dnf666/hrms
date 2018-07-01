package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.model.StockVO;
import com.facishare.crm.stock.predefine.manager.StockLogManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author linchf
 * @date 2018/4/23
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockLogManagerTest extends BaseTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private StockLogManager stockLogManager;

    @Test
    public void testCalculateStockVOsBySalesOrderId() {
        List<StockVO> stockVOs = stockLogManager.calculateStockVOsBySalesOrderId("55988", "62766326b18f417db2982bd2db9e6b78");
     }
}

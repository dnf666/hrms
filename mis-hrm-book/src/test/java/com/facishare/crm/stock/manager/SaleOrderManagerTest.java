package com.facishare.crm.stock.manager;

import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.stock.predefine.manager.SaleOrderManager;
import com.facishare.paas.appframework.core.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linchf on 2018/2/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class SaleOrderManagerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }
    @Resource
    private SaleOrderManager saleOrderManager;

    @Test
    public void testGetByIds() {
        List<SalesOrderModel.SalesOrderVo> result = saleOrderManager.getByIds(new User("55985", "1000"), Arrays.asList("e22d3a2706b6458e8900e2a266d3c530"));
        System.out.println(result);
    }
}

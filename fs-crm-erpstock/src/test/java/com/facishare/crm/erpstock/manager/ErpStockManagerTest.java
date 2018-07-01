package com.facishare.crm.erpstock.manager;

import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.paas.appframework.core.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author linchf
 * @date 2018/5/9
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class ErpStockManagerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private ErpStockManager erpStockManager;

    @Test
    public void testIsStockEnable() {
        erpStockManager.isStockEnable(new User("55985", "1000"));
    }

}

package com.facishare.crm.stock.manager;


import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.predefine.manager.ReturnOrderManager;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by linchf on 2018/1/16.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ReturnOrderManagerTest extends BaseTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private ReturnOrderManager returnOrderManager;

    @Test
    public void testGetById() {
        returnOrderManager.getById(new User("2", "1000"), "b7ed6db03125410fa48eee5d79214dd7");
    }

    @Test
    public void testGetByOrderId() {
        returnOrderManager.getBySalesOrderId(new User("55985", "1000"), "516f3ba9764e4475bc1e2b2a1ee73ee3");
    }

}

package com.facishare.crm.promotion.manager;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.predefine.manager.SalesOrderManager;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class SalesOrderManagerTest {
    @Autowired
    private SalesOrderManager salesOrderManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void listByPromtionIdsTest() {
        List list = salesOrderManager.listRelatedPromotionObjectsByPromotionIds(new User("2", "1000"), Lists.newArrayList("5a55abea830bdbc4a5fa0a44"));
        System.out.println(list);
    }
}

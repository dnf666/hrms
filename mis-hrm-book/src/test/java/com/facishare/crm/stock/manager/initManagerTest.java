package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.predefine.manager.InitManager;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.paas.appframework.core.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author liangk
 * @date 13/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class initManagerTest extends BaseTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private InitManager initManager;

    @Test
    public void initRequisitionTest() {
        StockType.EnableRequisitionResult result = initManager.initRequisition(user);
        System.out.println(result);
    }

    @Test
    public void initTest() {
        initManager.init(new User("55985", "1000"));
    }
}

package com.facishare.crm.manager;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CustomerAccountManagerTest {
    @Autowired
    private CustomerAccountManager customerAccountManager;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    String tenantId = "";
    String fsUserId = "1000";

    @Before
    public void initUser() {
        String environment = System.getProperty("spring.profiles.active");
        if ("ceshi113".equals(environment)) {
            tenantId = "55732";
        } else if ("fstest".equals(environment)) {
            tenantId = "7";
        }
    }

    @Test
    public void getCustomerAccountIncludeInvalidByCustomerIdTest() {
        User user = new User(tenantId, fsUserId);
        String customerId = "";
        Optional<IObjectData> objectData = customerAccountManager.getCustomerAccountIncludeInvalidByCustomerId(user, customerId);
        System.out.println(objectData);
    }

    /*    @Test
    public void test() {
        Map<String, String> map = customerAccountManager.test(new User("68867", "1000"), 0, 100);
        System.out.println(map);
    
    }*/
}

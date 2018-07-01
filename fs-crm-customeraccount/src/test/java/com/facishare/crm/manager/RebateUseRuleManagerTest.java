package com.facishare.crm.manager;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.predefine.manager.RebateUseRuleManager;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateUseRuleManagerTest {
    @Autowired
    private RebateUseRuleManager rebateUseRuleManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void getByCustomerIdTest() {
        String customerId = "36701b64b1344b499b56b4a47f816e3c";
        rebateUseRuleManager.getRebateUseRuleByCustomerId(new User("55910", "1000"), customerId);
    }

    @Test
    public void validateTest() {
        String customerId = "ad71b92b6f3b4efa956d4e6ca0c3b624";
        Map<String, BigDecimal> orderIdMap = Maps.newHashMap();
        orderIdMap.put("d2c28784f2ed41a58f20d8787316318f", BigDecimal.valueOf(200.0));
        User user = new User("2", "1000");
        rebateUseRuleManager.validate(user, customerId, orderIdMap);
    }
}

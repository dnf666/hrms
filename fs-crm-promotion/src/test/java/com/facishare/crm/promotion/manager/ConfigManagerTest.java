package com.facishare.crm.promotion.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.predefine.manager.TenantConfigManager;
import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.paas.appframework.core.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ConfigManagerTest {
    @Autowired
    private TenantConfigManager configManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void get() {
        PromotionType.PromotionSwitchEnum tenantConfig = configManager.getPromotionStatus("2");
        System.out.print(tenantConfig);
    }

    @Test
    public void udpateTest() {
        int result = configManager.updatePromotionStatus(new User("2", "1000"), PromotionType.PromotionSwitchEnum.OPENED);
        System.out.println(result);
    }

}

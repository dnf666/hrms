package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class TenantQuotaManagerTest {
    @Resource
    private TenantQuotaManager tenantQuotaManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void addUsedQuota_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User user = new User("55988", "1000");
        tenantQuotaManager.addUsedQuota(user, "1");
    }
}
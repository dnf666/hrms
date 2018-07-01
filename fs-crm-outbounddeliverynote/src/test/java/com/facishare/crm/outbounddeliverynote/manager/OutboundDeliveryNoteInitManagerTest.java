package com.facishare.crm.outbounddeliverynote.manager;

import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteInitManager;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class OutboundDeliveryNoteInitManagerTest {
    @Resource
    private OutboundDeliveryNoteInitManager outboundDeliveryNoteInitManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testInit() {
        outboundDeliveryNoteInitManager.init(new User("55985", "1000"));
    }

    @Test
    public void testInitTemplate() {
        outboundDeliveryNoteInitManager.initPrintTemplate(new User("55983", "1000"));
    }
}

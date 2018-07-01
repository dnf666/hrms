package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.manager.GrayReleaseManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static junit.framework.TestCase.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class GrayReleaseManagerTest {
    @Resource
    private GrayReleaseManager grayReleaseManager;

    static {
        System.setProperty("process.profile", "ceshi113");
    }

    @Test
    public void isAllow() throws Exception {
        assertTrue(grayReleaseManager.isInitSwitchGrayed("55983"));
        assertTrue(!grayReleaseManager.isInitSwitchGrayed("55985"));
    }
}
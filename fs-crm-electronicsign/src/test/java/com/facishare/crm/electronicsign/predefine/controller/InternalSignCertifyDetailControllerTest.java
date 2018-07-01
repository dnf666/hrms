package com.facishare.crm.electronicsign.predefine.controller;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.predefine.base.BaseControllerTest;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class InternalSignCertifyDetailControllerTest extends BaseControllerTest {

    static {
        System.setProperty("process.profile", "ceshi113");
    }

    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = "1000";
    }

    public InternalSignCertifyDetailControllerTest() {
        super(InternalSignCertifyObjConstants.API_NAME);
    }

    @Test
    public void objectDetail_Success() {
        String argJson = "{\"objectDataId\":\"5afc0fdebab09c65776cbf9d\",\"objectDescribeApiName\":\"InternalSignCertifyObj\"}";
        StandardDetailController.Arg arg = new Gson().fromJson(argJson, StandardDetailController.Arg.class);
        StandardDetailController.Result result = executeDetail(arg);
        System.out.println(result);
    }
}

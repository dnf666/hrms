package com.facishare.crm.deliverynote.controller;

import com.facishare.crm.deliverynote.base.BaseControllerTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class DeliveryNoteProductListHeaderControllerTest extends BaseControllerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }


    public DeliveryNoteProductListHeaderControllerTest() {
        super(DeliveryNoteProductObjConstants.API_NAME);
    }

    public void initUser() {
        this.tenantId = "55988";
        this.fsUserId = "1001";
    }

    @Test
    public void listHeaderTest() {
        String argJson = "{\"include_layout\":true,\"apiname\":\"DeliveryNoteProductObj\",\"layout_type\":\"list\",\"recordType_apiName\":\"default__c\"}";
        StandardListHeaderController.Arg arg = new Gson().fromJson(argJson, StandardListHeaderController.Arg.class);
        StandardListHeaderController.Result result = executeListHeader(arg);
        Assert.assertTrue(result.getLayout().toLayout().getButtons().isEmpty());
    }
}

package com.facishare.crm.promotion.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.BatchGetPromotionProductQuantity;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class CrmRemoteTest {
    @Autowired
    private CrmRestApi crmRestApi;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    private String tenantId = "2";
    private String userId = "1000";

    @Test
    public void getPromotionQuantityTest() {
        BatchGetPromotionProductQuantity.Arg arg = new BatchGetPromotionProductQuantity.Arg();
        BatchGetPromotionProductQuantity.PromotionProductArg promotionProductArg = new BatchGetPromotionProductQuantity.PromotionProductArg();
        promotionProductArg.setProductId("5a69983e830bdb9093ff8799");
        promotionProductArg.setPromotionId("5a69983e830bdb9093ff8792");
        //        arg.setPromotionProductArgs(Lists.newArrayList(promotionProductArg));
        BatchGetPromotionProductQuantity.Result result = crmRestApi.getPromotionQuantiy(Lists.newArrayList(promotionProductArg), getHeader(tenantId, userId));
        System.out.println(result);
    }

    public Map<String, String> getHeader(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        return headers;
    }
}

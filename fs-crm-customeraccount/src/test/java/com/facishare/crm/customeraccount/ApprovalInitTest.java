package com.facishare.crm.customeraccount;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.crm.rest.dto.GetCurInstanceStateModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class ApprovalInitTest {
    @Autowired
    private ApprovalInitProxy approvalInitProxy;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Test
    public void initApprovalTest() {
        ApprovalInitModel.Arg arg = new ApprovalInitModel.Arg();
        arg.setEntityId("object_oxmr2__c");
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", "1000");
        headers.put("x-tenant-id", "55732");
        ApprovalInitModel.Result result = approvalInitProxy.init(arg, headers);
        System.out.println(result);
    }

    @Test
    public void approvalInstanceTest() {
        String customerId = "1f13b3099d2743399bcb01e112ddfe40";
        String tenantId = "68867";
        String userId = "1000";
        GetCurInstanceStateModel.Arg arg = new GetCurInstanceStateModel.Arg();
        arg.setObjectIds(Lists.newArrayList(customerId));
        GetCurInstanceStateModel.Result result = approvalInitProxy.getCurInstanceStateByObjectIds(arg, getApprovalInitHeaders(tenantId, userId));
        System.out.println(result);
    }

    private Map<String, String> getApprovalInitHeaders(String tenantId, String fsUserId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", fsUserId);
        headers.put("x-tenant-id", tenantId);
        return headers;
    }
}

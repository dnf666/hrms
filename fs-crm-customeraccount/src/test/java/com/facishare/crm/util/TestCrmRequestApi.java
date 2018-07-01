package com.facishare.crm.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.QueryCustomersByPage;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/10/17.
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class TestCrmRequestApi extends BaseServiceTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private CrmRestApi crmRequestApi;

    public TestCrmRequestApi() {
        super("TestCrmRequestApi");
    }

    private Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

    private ServiceContext getServiceContextNew() {
        return newServiceContext();
    }

    /**
     *
     */
    @Test
    public void testQueryCustomers() {
        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();
        int offset = 0;
        int limit = 10;

        QueryCustomersByPage.Arg arg = new QueryCustomersByPage.Arg();
        arg.setOffset(offset);
        arg.setLimit(limit);
        arg.setCustomerIDs(Lists.newArrayList("customerIds"));
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        QueryCustomersByPage.Result queryCustomersByPageResult = crmRequestApi.queryCustomersByPage(arg, headers);
        log.info("result={}", queryCustomersByPageResult);
    }

    @Test
    public void test() {
        SalesOrderModel.GetByIdsResult getByIdsResult = crmRequestApi.getCustomerOrderByIds(new String[] { "bdba9457803c40e5bead599b3440d0e2" }, getHeaders("2", "1000"));
        System.out.println(getByIdsResult);
    }

}

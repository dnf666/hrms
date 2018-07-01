package com.facishare.crm.customeraccount;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseControllerTest;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.paas.appframework.core.predef.controller.BaseListController.Result;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateOutcomeDetailControllerTest extends BaseControllerTest {

    public RebateOutcomeDetailControllerTest() {
        super(RebateOutcomeDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Test
    public void testDetail() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a23692ca5083d2fe204028f");
        arg.setObjectDescribeApiName(apiName);
        StandardDetailController.Result result = executeDetail(arg);
        System.out.println(result);
    }

    @Test
    public void testList() {
        StandardListController.Arg arg = new StandardListController.Arg();
        arg.setObjectDescribeApiName(apiName);
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");
        arg.setSearchTemplateId("");
        Result result = executeList(arg);
        System.out.println(result);
    }

}

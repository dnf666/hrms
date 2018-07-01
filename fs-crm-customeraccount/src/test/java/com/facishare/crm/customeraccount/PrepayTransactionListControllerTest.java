package com.facishare.crm.customeraccount;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseControllerTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.paas.appframework.core.predef.controller.BaseListController.Result;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrepayTransactionListControllerTest extends BaseControllerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public PrepayTransactionListControllerTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    @Test
    public void testDetail() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a1c017dbab09c6d9b1aa1e1");
        arg.setObjectDescribeApiName(apiName);
        StandardDetailController.Result result = executeDetail(arg);
        System.out.println(result);
    }

    @Test
    public void testList() {
        StandardListController.Arg arg = new StandardListController.Arg();
        arg.setObjectDescribeApiName(apiName);
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");
        arg.setSearchTemplateId("59f2d83b830bdb9ba6b9300f");
        Result result = executeList(arg);
        System.out.println(result);
    }

    @Test
    public void testDesLayout() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(PrepayDetailConstants.API_NAME);
        arg.setRecordType_apiName("income_record_type__c");
        arg.setInclude_layout(true);
        arg.setLayout_type("detail");
        arg.setInclude_detail_describe(true);
        Object result = execute("DescribeLayout", arg);
        System.out.println(result);
    }

    @Test
    public void testRelate() {
        StandardRelatedController.Arg arg = new StandardRelatedController.Arg();
        arg.setObjectDataId("5a0193e8767fb32c9411a8cc");
        arg.setLimit(2);
        arg.setOffset(0);
        arg.setObjectDescribeApiName(apiName);
        Object result = execute("Related", arg);
        System.out.println(result);
    }

}

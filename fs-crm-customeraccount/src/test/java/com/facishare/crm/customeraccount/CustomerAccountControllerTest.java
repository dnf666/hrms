package com.facishare.crm.customeraccount;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseControllerTest;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.paas.appframework.core.predef.controller.BaseListController.Result;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.rest.proxy.util.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CustomerAccountControllerTest extends BaseControllerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public CustomerAccountControllerTest() {
        super(CustomerAccountConstants.API_NAME);
    }

    @Test
    public void testDetail() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a253e4ba5083d6f72771b57");
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
        String json = JsonUtil.toJson(result.getDataList());
        System.out.println("result=" + result);
    }

    @Test
    public void testEditLayout() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(apiName);
        arg.setData_id("5a3b1ad9bab09cdeb9fdd48e");
        arg.setInclude_detail_describe(true);
        arg.setLayout_type("edit");
        arg.setRecordType_apiName("default__c");
        arg.setInclude_layout(true);
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

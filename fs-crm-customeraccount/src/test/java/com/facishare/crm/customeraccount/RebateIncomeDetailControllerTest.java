package com.facishare.crm.customeraccount;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseControllerTest;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.paas.appframework.core.predef.controller.BaseListController.Result;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateIncomeDetailControllerTest extends BaseControllerTest {

    public RebateIncomeDetailControllerTest() {
        super(RebateIncomeDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Test
    public void testDetail() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a1d13a9bab09cc8a87b7e9f");
        arg.setObjectDescribeApiName(apiName);
        StandardDetailController.Result result = executeDetail(arg);
        System.out.println(result);
    }

    @Test
    public void testList() {
        StandardListController.Arg arg = new StandardListController.Arg();
        arg.setObjectDescribeApiName(apiName);
        arg.setSearchQueryInfo("{\"limit\":1,\"offset\":0,\"filters\":[]}");
        arg.setSearchTemplateId("");
        arg.setIncludeLayout(true);
        Result result = executeList(arg);
        System.out.println(result);
    }

    @Test
    public void testListHeader() {
        StandardListHeaderController.Arg arg = new StandardListHeaderController.Arg();
        arg.setApiName(RebateIncomeDetailConstants.API_NAME);
        arg.setIncludeLayout(true);
        arg.setRecordTypeAPIName("default__c");
        arg.setLayoutType("list");
        arg.setIncludeRefDescribe(true);
        StandardListHeaderController.Result result = executeListHeader(arg);
        System.out.println(result);

    }

    @Test
    public void testIncomeLayout() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(RebateIncomeDetailConstants.API_NAME);
        arg.setInclude_layout(true);
        arg.setData_id("5a2f89a9a5083d966b587924");
        arg.setInclude_detail_describe(true);
        arg.setLayout_type("edit");
        arg.setRecordType_apiName("default__c");
        arg.setInclude_layout(true);
        Object result = execute("DescribeLayout", arg);
        System.out.println(result);

    }

    @Test
    public void layoutTest() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(apiName);
        //        arg.setData_id("5a095304bab09cc3615b2847");
        arg.setRecordType_apiName("default__c");
        arg.setInclude_layout(true);
        arg.setLayout_type("add");
        arg.setInclude_detail_describe(true);
        Object result = execute("DescribeLayout", arg);
        System.out.println(result);
    }

    @Test
    public void relatedListTest() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();
        arg.setSearchQueryInfo("{\"offset\":0,\"wheres\":[],\"limit\":20,\"orders\":[],\"filters\":[]}");
        arg.setIncludeAssociated(true);
        arg.setTargetObjectApiName("CustomerAccountObj");
        arg.setTargetObjectDataId("5a5715baa5083d649325e2a2");
        arg.setRelatedListName(null);
        arg.setObjectApiName("RebateIncomeDetailObj");
        arg.setSearchTemplateId(null);
        Object result = execute("RelatedList", arg);
        System.out.println(result);
    }
}

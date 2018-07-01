package com.facishare.crm.promotion.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.base.BaseControllerTest;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PromotionControllerTest extends BaseControllerTest {
    public PromotionControllerTest() {
        super(PromotionConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void describeTest() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(apiName);
        arg.setInclude_detail_describe(true);
        arg.setLayout_type("add");
        arg.setRecordType_apiName(PromotionRecordTypeEnum.ProductPromotion.apiName);
        arg.setInclude_layout(true);
        //        arg.setData_id("5a9d2215830bdbacc813a19d");
        Object result = execute("DescribeLayout", arg);
        System.out.println(result);
    }

    @Test
    public void detailTest() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDescribeApiName(PromotionConstants.API_NAME);
        arg.setObjectDataId("5a5f3617830bdbd7f9fd4d6d");
        Object result = execute("Detail", arg);
        System.out.println(result);
    }

    @Test
    public void relatedListTest() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();
        //        arg.setObjectApiName(PromotionRuleConstants.API_NAME);
        arg.setObjectData(null);
        arg.setRelatedListName("target_related_list_promotion_rule_promotion");
        arg.setTargetObjectDataId("5a55abea830bdbc4a5fa0a44");
        arg.setTargetObjectApiName(PromotionConstants.API_NAME);
        arg.setIncludeAssociated(true);
        arg.setSearchQueryInfo("{\"limit\":10000,\"offset\":0}");
        Object result = execute("RelatedList", arg);
        System.out.println(result);
    }

    @Test
    public void listHeaderTest() {
        StandardListHeaderController.Arg arg = new StandardListHeaderController.Arg();
        arg.setApiName(PromotionConstants.API_NAME);
        arg.setLayoutType("list");
        arg.setIncludeLayout(true);
        arg.setRecordTypeAPIName("product_promotion__c");
        Object result = execute("ListHeader", arg);
        System.out.println(result);
    }
}

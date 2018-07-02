package com.facishare.crm.promotion.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.base.BaseControllerTest;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PromotionRuleControllerTest extends BaseControllerTest {

    public PromotionRuleControllerTest() {
        super(PromotionRuleConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void relatedListTest() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();
        arg.setObjectApiName(PromotionRuleConstants.API_NAME);
        arg.setObjectData(null);
        arg.setRelatedListName("target_related_list_promotion_rule_promotion");
        arg.setTargetObjectDataId("5a5c7752830bdb232000b502");
        arg.setTargetObjectApiName(PromotionConstants.API_NAME);
        arg.setIncludeAssociated(true);
        arg.setSearchQueryInfo("{\"limit\":10000,\"offset\":0}");
        Object result = execute("RelatedList", arg);
        System.out.println(result);
    }
}

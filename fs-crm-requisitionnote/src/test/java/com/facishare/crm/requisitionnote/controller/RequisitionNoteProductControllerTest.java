package com.facishare.crm.requisitionnote.controller;

import com.facishare.crm.requisitionnote.base.BaseControllerTest;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author liangk
 * @date 22/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RequisitionNoteProductControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public RequisitionNoteProductControllerTest() {super(RequisitionNoteProductConstants.API_NAME);}

    @Test
    public void testRelatedList() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();

        arg.setObjectApiName(RequisitionNoteProductConstants.API_NAME);
        arg.setTargetObjectApiName(RequisitionNoteConstants.API_NAME);
        arg.setTargetObjectDataId("5a61bf0f830bdb80fb99d9a1");
        arg.setSearchQueryInfo("{\"limit\":10000,\"offset\":0}");
        arg.setIncludeAssociated(true);
        arg.setRelatedListName("target_related_list_product_note");

        Object result = execute(StandardController.RelatedList.name(), arg);
        System.out.println(result);

    }

    @Test
    public void testListHeader() {
        StandardListHeaderController.Arg arg = new StandardListHeaderController.Arg();

        arg.setApiName(RequisitionNoteProductConstants.API_NAME);
        arg.setIncludeLayout(true);
        arg.setLayoutType("list");
        arg.setRecordTypeAPIName("");

        Object result = executeListHeader(arg);
        System.out.println(result);
    }
}

package com.facishare.crm.requisitionnote.controller;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.base.BaseControllerTest;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.api.MultiRecordType;
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
public class RequisitionNoteControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public RequisitionNoteControllerTest() {
        super(RequisitionNoteConstants.API_NAME);
    }

    @Test
    public void testDescribeLayoutController() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(RequisitionNoteConstants.API_NAME);
        arg.setInclude_layout(true);
        arg.setInclude_detail_describe(true);
        arg.setData_id("5acdeca8bab09c781cf09816");
        arg.setLayout_type(SystemConstants.LayoutType.Edit.layoutType);
        arg.setRecordType_apiName(MultiRecordType.RECORD_TYPE_DEFAULT);
        StandardDescribeLayoutController.Result result = (StandardDescribeLayoutController.Result) execute(StandardController.DescribeLayout.name(), arg);
        System.out.println(result);

    }

    @Test
    public void testDetailController() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5ad48736bab09cb8bd705038");
        arg.setObjectDescribeApiName(RequisitionNoteConstants.API_NAME);

        Object result = executeDetail(arg);
        System.out.println(result);
    }
}

package com.facishare.crm.stock.controller;

import com.facishare.crm.stock.base.BaseControllerTest;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
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
public class GoodsReceivedNoteControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public GoodsReceivedNoteControllerTest() {
        super(GoodsReceivedNoteConstants.API_NAME);
    }

    @Test
    public void testDescribeLayoutController() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname("GoodsReceivedNoteObj");
        arg.setInclude_layout(true);
        arg.setInclude_detail_describe(true);
        arg.setLayout_type("edit");
        arg.setRecordType_apiName(MultiRecordType.RECORD_TYPE_DEFAULT);
        arg.setData_id("5a7bffa6830bdba15c58b4ee");

        Object result = execute(StandardController.DescribeLayout.name(), arg);
        System.out.println(result);
    }

    @Test
    public void testDetailController() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a70259c830bdb1a03650119");
        arg.setObjectDescribeApiName(apiName);

        Object result = executeDetail(arg);
        System.out.println(result);
    }
}

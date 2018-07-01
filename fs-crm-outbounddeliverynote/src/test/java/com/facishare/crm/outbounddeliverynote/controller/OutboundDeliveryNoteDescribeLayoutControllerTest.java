package com.facishare.crm.outbounddeliverynote.controller;

import com.facishare.crm.outbounddeliverynote.base.BaseControllerTest;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author linchf
 * @date 2018/3/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class OutboundDeliveryNoteDescribeLayoutControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }


    public OutboundDeliveryNoteDescribeLayoutControllerTest() {
        super(OutboundDeliveryNoteConstants.API_NAME);
    }


    @Test
    public void testDescribeLayoutController() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(OutboundDeliveryNoteConstants.API_NAME);
        arg.setInclude_layout(true);
        arg.setInclude_detail_describe(true);
        arg.setRecordType_apiName(OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName);
        arg.setLayout_type("add");

        Object result = execute(StandardController.DescribeLayout.name(), arg);
        System.out.println(result);
    }
}

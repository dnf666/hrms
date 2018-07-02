package com.facishare.crm.outbounddeliverynote.controller;

import com.facishare.crm.outbounddeliverynote.base.BaseControllerTest;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author linchf
 * @date 2018/3/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class OutboundDeliveryNoteDetailControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public OutboundDeliveryNoteDetailControllerTest() {
        super(OutboundDeliveryNoteConstants.API_NAME);
    }

    @Test
    public void testDetailController() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5aaa399e830bdbbfdd502baf");
        arg.setObjectDescribeApiName(apiName);

        Object result = executeDetail(arg);
        System.out.println(result);
    }

}

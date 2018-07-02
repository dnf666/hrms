package com.facishare.crm.deliverynote.controller;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.base.BaseControllerTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class DeliveryNoteDescribeLayoutControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public DeliveryNoteDescribeLayoutControllerTest() {
        super(DeliveryNoteObjConstants.API_NAME);
    }

    public void initUser() {
        this.tenantId = "55983";
        this.fsUserId = "1000";
    }

    @Test
    public void describeLayoutTest() {
        StandardDescribeLayoutController.Arg arg = new StandardDescribeLayoutController.Arg();
        arg.setApiname(DeliveryNoteObjConstants.API_NAME);
        arg.setData_id("5abde7bd11ef525340b65fd8");
        arg.setInclude_layout(true);
        arg.setInclude_detail_describe(true);
        arg.setLayout_type(SystemConstants.LayoutType.Add.layoutType);
        StandardDescribeLayoutController.Result result = (StandardDescribeLayoutController.Result) execute(StandardController.DescribeLayout.name(), arg);
        log.info("resout", result);
    }
}

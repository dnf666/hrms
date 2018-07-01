package com.facishare.crm.deliverynote.action;

import com.facishare.crm.deliverynote.base.BaseActionTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chenzs on 2018/1/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class DeliveryNoteFlowCompleteActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public DeliveryNoteFlowCompleteActionTest() {
        super(DeliveryNoteProductObjConstants.API_NAME);
    }

    @Test
    public void testWorkFlowCompletedAction() {
        StandardFlowCompletedAction.Arg arg = new StandardFlowCompletedAction.Arg();
        arg.setDataId("5a698533830bdb7e44678fb1");
        arg.setDescribeApiName(DeliveryNoteProductObjConstants.API_NAME);
        arg.setStatus("pass");
        arg.setTriggerType(3);    //com.facishare.paas.appframework.flow.ApprovalFlowTriggerType
        arg.setTenantId("55988");
        arg.setUserId("1000");

        execute(StandardAction.FlowCompleted.name(), arg);
    }
}

package com.facishare.crm.manager;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateOutcomeManagerTest {
    @Autowired
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    @Autowired
    private ServiceFacade serviceFacade;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }
    String tenantId = "70233";
    String fsUserId = "1000";

    @Test
    public void queryTest() {
        List<String> ids = Lists.newArrayList("5a0fe2b9422c903b0c828310");
        String message = Joiner.on(",").join(ids);
        List<IObjectData> result = rebateOutcomeDetailManager.listInvalidDataByIds(new User(tenantId, fsUserId), ids);
        System.out.print(result);
    }

    @Test
    public void bulkInvalidTest() {
        String id = "5ac4a0507cfed9d3a94026b7";
        User user = new User(tenantId, fsUserId);
        IObjectData objectData = serviceFacade.findObjectData(user, id, RebateOutcomeDetailConstants.API_NAME);
        List<IObjectData> objectDataList = rebateOutcomeDetailManager.bulkInvalid(new User(tenantId, fsUserId), Lists.newArrayList(objectData), "invalid");
        System.out.println(objectDataList);

    }
}

package com.facishare.crm.customeraccount;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateOutcomeDetailAddActionTest extends BaseActionTest {

    public RebateOutcomeDetailAddActionTest() {
        super(RebateOutcomeDetailConstants.API_NAME);
    }

    @Autowired
    protected ServiceFacade serviceFacade;

    @Test
    public void addRebateOutcomeDetail() {
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, "59fc2585bab09c4755b55f3f");
        objectData.set(RebateOutcomeDetailConstants.Field.Amount.apiName, "100");
        objectData.set(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(RebateOutcomeDetailConstants.Field.Payment.apiName, "dac46996916341828c6b1ba9cc86f646");
        Object result = executeAdd(objectData);
        System.out.println("result=" + result);
    }
}

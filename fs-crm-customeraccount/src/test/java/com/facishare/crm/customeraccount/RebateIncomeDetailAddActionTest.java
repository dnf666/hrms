package com.facishare.crm.customeraccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.predefine.service.dto.BulkInvalidModel;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateIncomeDetailAddActionTest extends BaseActionTest {

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Autowired
    protected ServiceFacade serviceFacade;

    public RebateIncomeDetailAddActionTest() {
        super(RebateIncomeDetailConstants.API_NAME);
    }

    @Test
    public void testRecover() {
        StandardBulkRecoverAction.Arg arg = new StandardBulkRecoverAction.Arg();
        arg.setObjectDescribeAPIName(RebateIncomeDetailConstants.API_NAME);
        arg.setIdList(Lists.newArrayList("5a1d4f19a5083d6266b50d8f"));
        Object result = execute("BulkRecover", arg);
        System.out.println(result);
    }

    @Test
    public void invalid() {
        IObjectData o = new ObjectData();
        o.setId("5a0fa5f2bab09c0a72501acd");
        executeInvalid(o);
    }

    @Test
    public void flowCompleted() {
        StandardFlowCompletedAction.Arg arg = new StandardFlowCompletedAction.Arg();
        arg.setTriggerType(1);
        arg.setStatus("pass");
        arg.setTenantId("71705");
        arg.setDescribeApiName("RebateIncomeDetailObj");
        arg.setDataId("5aec346f7cfed95e40814b9d");
        arg.setUserId("1000");
        executeFlowCompleted(arg);
    }

    @Test
    public void bulkInvalid() {
        BulkInvalidModel.Arg arg = new BulkInvalidModel.Arg();
        BulkInvalidModel.InvalidArg invalidArg = new BulkInvalidModel.InvalidArg();
        invalidArg.setId("5a335833a5083d0a917b1926");
        List<BulkInvalidModel.InvalidArg> list = new ArrayList<>();
        list.add(invalidArg);
        arg.setDataList(list);
        executeBulkInvalid(arg);
    }

    @Test
    public void testFlowCompleteAction() {
        StandardFlowCompletedAction.Arg arg = new StandardFlowCompletedAction.Arg();
        arg.setDataId("5ad9c27a7cfed93eb961d35d");
        arg.setDescribeApiName(RebateIncomeDetailConstants.API_NAME);
        arg.setStatus("pass");
        arg.setTriggerType(1);
        arg.setTenantId("71560");
        arg.setUserId("1000");

        execute(StandardAction.FlowCompleted.name(), arg);
    }

    @Test
    public void addRebateIncomeDetail() {
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set(RebateIncomeDetailConstants.Field.Customer.apiName, "1ba8e6db056b41cabd7a25d1fabc0b42");
        objectData.set(RebateIncomeDetailConstants.Field.Amount.apiName, "79.00");
        objectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, "0.00");
        objectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, "79.00");
        objectData.set(RebateIncomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.StartTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.EndTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.Other.getValue());
        objectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName + "__o", "asdfa");

        //objectData.set(RebateIncomeDetailConstants.Field.Refund.apiName, "512a2f4ebbc544a8bcbec5eb41868ed7");
        //        objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        Object result = executeAdd(objectData);
        System.out.println(result);
    }
}

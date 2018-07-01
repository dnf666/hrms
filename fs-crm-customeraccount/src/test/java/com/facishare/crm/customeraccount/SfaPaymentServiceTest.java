package com.facishare.crm.customeraccount;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.service.SfaPaymentService;
import com.facishare.crm.customeraccount.predefine.service.SfaRefundService;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaCreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaFlowCompleteModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class SfaPaymentServiceTest extends BaseServiceTest {

    static {
        //System.setProperty("spring.profiles.active", "fstest");
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    SfaPaymentService sfaPaymentService;
    @Autowired
    SfaRefundService sfaRefundService;

    public SfaPaymentServiceTest() {
        super(RebateOutcomeDetailConstants.API_NAME);
    }

    @Test
    public void test() {
        String id = "5a684fcb830bdba04cd9505b";

        IObjectData objectData = serviceFacade.findObjectData(new User(tenantId, fsUserId), id, PrepayDetailConstants.API_NAME);
        ObjectDataDocument objectDataDocument = ObjectDataDocument.of(objectData);
        objectDataDocument.remove("relevant_team");
        System.out.println(objectData);

    }

    @Test
    public void create() {
        SfaCreateModel.Arg arg = new SfaCreateModel.Arg();
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set(RebateOutcomeDetailConstants.Field.Amount.apiName, "2.0");
        objectData.set(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(RebateOutcomeDetailConstants.Field.Payment.apiName, "1b13ad363d5a4aae9236d4c9017479e7");
        objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        objectData.set("customer_id", "a705703a45fd4f338fc393650a9a0fdf");
        arg.setRebateOutcomeDetailData(ObjectDataDocument.of(objectData));
        SfaCreateModel.Result result = sfaPaymentService.create(arg, newServiceContext());
        Assert.assertNotNull(result);

    }

    @Test
    public void bulkInvalidPayment() {
        SfaBulkInvalidModel.Arg arg = new SfaBulkInvalidModel.Arg();
        //arg.setDataIds(Lists.newArrayList("a95d510f31d0446eb112fa7dfccfa319"));
        arg.setDataIds(Lists.newArrayList("cd3733128c214a8a96a2b9817cdd18d6"));
        arg.setLifeStatus("invalid");
        sfaPaymentService.bulkInvalid(arg, newServiceContext());
    }

    @Test
    public void testBulkDeteteByPayment() {
        //select t.* from prepay_detail t where t.payment_id = 'bba92024b6b3419585ba9dcf26142b9d';  -- 5a28fb3aa5083da25202185f
        //FIXME 
    }

    @Test
    public void bulkInvalidRefund() {
        SfaBulkInvalidModel.Arg arg = new SfaBulkInvalidModel.Arg();
        arg.setDataIds(Lists.newArrayList("578be1deae264e99979cf8997e8bf184"));
        arg.setLifeStatus("in_change");
        sfaRefundService.bulkInvalid(arg, newServiceContext());
    }

    @Test
    public void flowComplete() {
        SfaFlowCompleteModel.Arg arg = new SfaFlowCompleteModel.Arg();
        arg.setLifeStatus(SystemConstants.LifeStatus.UnderReview.value);
        arg.setDataId("578be1deae264e99979cf8997e8bf184"); // 578be1deae264e99979cf8997e8bf184
        arg.setApprovalType(ApprovalFlowTriggerType.UPDATE.getId());
        sfaRefundService.flowComplete(arg, newServiceContext());
    }
}

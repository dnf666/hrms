package com.facishare.crm.customeraccount;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.service.RebateOutcomeDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.paas.appframework.core.model.ServiceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RebateOutcomeServiceTest extends BaseServiceTest {

    public RebateOutcomeServiceTest() {
        super(RebateIncomeDetailConstants.API_NAME);
    }

    @Autowired
    RebateOutcomeDetailService rebateOutcomeDetailService;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    /**
      2017-11-6 21:26:17测试通过<br>
     */
    @Test
    public void testCreateRebateIncome() {

        //		IObjectData objectData = new ObjectData();
        //		objectData.setRecordType("default__c");
        //		objectData.set(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, "59fc1f75e567bf2a740333a8");
        //		objectData.set(RebateOutcomeDetailConstants.Field.Amount.apiName, "1");
        //		objectData.set(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        //		objectData.set(RebateOutcomeDetailConstants.Field.Payment.apiName, "45434555f0a64d988e65dcc06492b034");
        //		Object result = executeAdd(objectData);
        //		System.out.println(result);

        //		RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        //		ServiceContext serviceContext = getServiceContextNew();
        //		String customerId = "eebe39d4fca743ed80802825279353f8";
        //		IObjectData objectData = new ObjectData();
        //		objectData.setRecordType("default__c");
        //		objectData.set(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, "59fc1f75e567bf2a740333a8");
        //
        //		objectData.set(RebateIncomeDetailConstants.Field.Customer.apiName, customerId);
        //		objectData.set(RebateIncomeDetailConstants.Field.Amount.apiName, "200.00");
        //		objectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, "0.00");
        //		objectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, "200.00");
        //		objectData.set(RebateIncomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        //		objectData.set(RebateIncomeDetailConstants.Field.StartTime.apiName, new Date().getTime());
        //		objectData.set(RebateIncomeDetailConstants.Field.EndTime.apiName, new Date().getTime());
        //		objectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.OnlineCharge.getValue());
        //		objectData.set(RebateIncomeDetailConstants.Field.Refund.apiName, "512a2f4ebbc544a8bcbec5eb41868ed7");
        //		objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        //
        //		objectData.setDescribeApiName(RebateIncomeDetailConstants.API_NAME); //59c9b628422c901504ee4ecc
        //		objectData.setDescribeId(Id.parse(getObjectDescribeId()));
        //
        //		CreateModel.OutcomeCreateArg arg = new CreateModel.OutcomeCreateArg();
        //		arg.setObjectData(ObjectDataDocument.of(objectData));
        //		CreateModel.ResultList result = rebateOutcomeDetailService.create(arg, serviceContext);
        //		System.out.println(result);
    }

    private ServiceContext getServiceContextNew() {
        return newServiceContext();
    }

    @Test
    public void listByRebateIncomeId() {
        ServiceContext serviceContext = getServiceContextNew();
        ListByIdModel.RebateOutcomeArg arg = new ListByIdModel.RebateOutcomeArg();
        arg.setId("5a95406d7cfed93ca46070f0");
        arg.setPageNumber(1);
        arg.setPageSize(5);
        ListByIdModel.Result result = rebateOutcomeDetailService.listByRebateIncomeId(arg, serviceContext);
        result.getObjectDatas().forEach(o -> {
            System.out.println(o.toObjectData().getCreateTime());
        });
        Assert.assertNotNull(result);

    }

}

package com.facishare.crm.customeraccount;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.predefine.service.RebateIncomeDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.impl.ObjectData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RebateIncomeServiceTest extends BaseServiceTest {

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Autowired
    RebateIncomeDetailService rebateIncomeDetailService;

    @Autowired
    private IObjectDescribeService objectDescribeService;

    public RebateIncomeServiceTest() {
        super(RebateIncomeDetailConstants.API_NAME);
    }

    /**
    * 2017-11-04 测试OK<br>
    * @throws Exception
    */
    @Test
    public void listRebateIncomeByCustomerId() throws Exception {
        ServiceContext serviceContext = newServiceContext();
        ListByIdModel.RebateArg arg = new ListByIdModel.RebateArg();
        arg.setId("0c24510150e64315b63ca1f96816e71e");
        //arg.setCreateTime(1512961236077L);
        //arg.setCreateTimeEnd(1512961236077L);
        arg.setIncomeType("other");
        arg.setPageNumber(1);
        arg.setPageSize(10);
        ListByIdModel.Result result = rebateIncomeDetailService.listByCustomerId(arg, serviceContext);
        result.getObjectDatas().forEach(objectDataDocument -> {
            System.out.println(objectDataDocument.toObjectData().get("create_time", Long.class));
        });
        Assert.assertNotNull(result);
    }

    /**
      2017-11-6 21:26:17测试通过<br>
     */
    @Test
    public void testCreateRebateIncome() {
        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();
        String customerId = "eebe39d4fca743ed80802825279353f8";
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set(RebateIncomeDetailConstants.Field.Customer.apiName, customerId);
        objectData.set(RebateIncomeDetailConstants.Field.Amount.apiName, "200.00");
        objectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, "0.00");
        objectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, "200.00");
        objectData.set(RebateIncomeDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.StartTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.EndTime.apiName, new Date().getTime());
        objectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.OnlineCharge.getValue());
        // 512a2f4ebbc544a8bcbec5eb41868ed7
        // 4b9d813687d249dbb2827ed05f886768
        objectData.set(RebateIncomeDetailConstants.Field.Refund.apiName, "4b9d813687d249dbb2827ed05f886768");
        objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);

        objectData.setDescribeApiName(RebateIncomeDetailConstants.API_NAME); //59c9b628422c901504ee4ecc
        objectData.setDescribeId("");

        CreateModel.Arg arg = new CreateModel.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        CreateModel.Result result = rebateIncomeDetailService.create(arg, serviceContext);
        System.out.println(result);
        // 2017年11月10日 "_id" -> "5a047ea4e142a195552f0383"
    }

    @Test
    public void create() {
        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();
        ObjectData objectData = new ObjectData();
        objectData.setDeleted(false);
        objectData.setCreatedBy(fsUserId);
        objectData.setRecordType("default__c");
        objectData.setTenantId("55732");
        objectData.set("lock_user", Arrays.asList(1000));
        objectData.set("owner", Arrays.asList("1000"));
        objectData.set("name", "2017-11-06");

        //客户id一定是存在的客户id<br>
        String customerId = "eebe39d4fca743ed80802825279353f8";
        objectData.set("customer_id", customerId);//51324dbe7c464590a10d02dd4b72c156  b339f6eb97c1450cb54432ea94761487  45f4a84f113c48daa209d87bcb07c442
        objectData.set("customer_account_id", "123456789");
        objectData.set("amount", 21.0);
        objectData.set("transaction_time", new Date());

        //新建退款页面抓取的 tradeRefundId 512a2f4ebbc544a8bcbec5eb41868ed7
        // 2017-11-06 创建的：512a2f4ebbc544a8bcbec5eb41868ed7  ||   对应退款编号：20171104165755
        // 2017-11-06  创建的退款：d451da377e584c6784d8631e4e22c457
        objectData.set("refund_id", "d451da377e584c6784d8631e4e22c457");

        //objectData.set("income_type", RebateIncomeTypeEnum.PaymentInvalid.getValue());
        objectData.set("online_charge_no", "12312");

        objectData.setDescribeApiName(RebateIncomeDetailConstants.API_NAME); //59c9b628422c901504ee4ecc
        objectData.setDescribeId("");

        objectData.set("life_status", SystemConstants.LifeStatus.Normal.value);

        CreateModel.Arg arg = new CreateModel.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        CreateModel.Result result = rebateIncomeDetailService.create(arg, serviceContext);
        Assert.assertNotNull(result);
    }

    private ServiceContext getServiceContextNew() {
        return newServiceContext();
    }

}

package com.facishare.crm.customeraccount;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.PrepayOutcomeTypeEnum;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.service.PrepayDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.FlowCompleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByPaymentIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByRefundIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PrepayServiceTest extends BaseServiceTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    PrepayDetailService prepayDetailService;
    @Autowired
    PrepayDetailManager prepayDetailManager;

    @Autowired
    ServiceFacade serviceFacade;

    public PrepayServiceTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    /**
     * 2017-11-04 测试OK<br>
     * @throws Exception
     */
    @Test
    public void create() throws Exception {

        //RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();
        ObjectData objectData = new ObjectData();
        //objectData.setDeleted(false);
        //objectData.setCreatedBy(fsUserId);
        objectData.setRecordType("income_record_type__c");
        objectData.setTenantId("55732");
        //objectData.set("lock_user", Arrays.asList(1000));
        //objectData.set("owner", Arrays.asList("1000"));
        //objectData.set("name", "1");

        //客户id一定是存在的客户id<br>
        String customerId = "a705703a45fd4f338fc393650a9a0fdf";
        // 5a051f5020df9c1fcacd6b3d  为 2017年11月10日 查出来的<br>
        objectData.set("customer_id", "a705703a45fd4f338fc393650a9a0fdf");//51324dbe7c464590a10d02dd4b72c156  b339f6eb97c1450cb54432ea94761487  45f4a84f113c48daa209d87bcb07c442
        //objectData.set("customer_account_id", "123124");
        objectData.set("amount", 50);
        objectData.set("transaction_time", new Date());

        //新建回款页面抓取的 tradePaymentId
        //objectData.set("payment_id", "4b9d813687d249dbb2827ed05f886768");

        //新建退款页面抓取的 tradeRefundId 512a2f4ebbc544a8bcbec5eb41868ed7
        //objectData.set("refund_id", "512a2f4ebbc544a8bcbec5eb41868ed7");

        objectData.set("income_type", PrepayIncomeTypeEnum.OnlineCharge.getValue());
        //objectData.set("outcome_type", PrepayOutcomeTypeEnum.ManualDeduction.getValue());
        objectData.set("online_charge_no", "12311112");

        //objectData.setDescribeApiName(PrepayDetailConstants.API_NAME); //59c9b628422c901504ee4ecc
        //describeIdStr 一定要从数据库中去取<br>
        //String describeIdStr = "59f2d4ca422c9023d88bf52d";
        //objectData.setDescribeId(Id.parse(describeIdStr));

        //objectData.set("life_status", SystemConstants.LifeStatus.Normal.value);
        CreateModel.Arg arg = new CreateModel.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        CreateModel.Result result = null;
        Assert.assertNotNull("prepayService->create->result" + result);
    }

    @Test
    public void createWithRefundId() {

    }

    /**
     * @throws Exception
     */
    @Test
    public void createWithNoPyamentId() throws Exception {

        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();
        ObjectData objectData = new ObjectData();
        objectData.setDeleted(false);
        objectData.setCreatedBy(fsUserId);
        objectData.setRecordType("default__c");
        objectData.setTenantId("55732");
        objectData.set("lock_user", Arrays.asList(1000));
        objectData.set("owner", Arrays.asList("1000"));
        objectData.set("name", "1");

        //客户id一定是存在的客户id<br>
        String customerId = "eebe39d4fca743ed80802825279353f8";
        // 5a051f5020df9c1fcacd6b3d  为 2017年11月10日 查出来的<br>
        objectData.set("customer_id", "43354695466c467d9a6935e084c13126");//51324dbe7c464590a10d02dd4b72c156  b339f6eb97c1450cb54432ea94761487  45f4a84f113c48daa209d87bcb07c442
        objectData.set("customer_account_id", "123124");
        objectData.set("amount", 113.0);
        objectData.set("transaction_time", new Date());

        //新建回款页面抓取的 tradePaymentId
        //objectData.set("payment_id", "4b9d813687d249dbb2827ed05f886768");

        //新建退款页面抓取的 tradeRefundId 512a2f4ebbc544a8bcbec5eb41868ed7
        //objectData.set("refund_id", "512a2f4ebbc544a8bcbec5eb41868ed7");

        //objectData.set("income_type", PrepayIncomeTypeEnum.OfflineCharge.getValue());
        objectData.set("outcome_type", PrepayOutcomeTypeEnum.ManualDeduction.getValue());
        objectData.set("online_charge_no", "12312");

        objectData.setDescribeApiName(PrepayDetailConstants.API_NAME); //59c9b628422c901504ee4ecc
        //describeIdStr 一定要从数据库中去取<br>
        String describeIdStr = "5a0c1d15422c903eb88e1311";
        objectData.setDescribeId(describeIdStr);

        objectData.set("life_status", SystemConstants.LifeStatus.Normal.value);
        CreateModel.Arg arg = new CreateModel.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        CreateModel.Result result = null;
        Assert.assertNotNull("prepayService->create->result" + result);
    }

    @Test
    public void listByCustomerId() throws Exception {
        ServiceContext serviceContext = getServiceContextNew();
        ListByIdModel.Arg arg = new ListByIdModel.Arg();
        //arg.setCreateTime(new Date().getTime());
        //arg.setLifeStatus("normal");
        //arg.setIncomeType("7");
        //arg.setRecordType("default__c");
        arg.setId("a29de461ac874d02995a470d782d7c88");
        arg.setPageNumber(1);
        arg.setPageSize(50);
        ListByIdModel.Result result = prepayDetailService.listByCustomerId(arg, serviceContext);
        result.getObjectDatas().forEach(objectDataDocument -> {
            IObjectData objectData = objectDataDocument.toObjectData();
            Long createTime = objectData.get("create_time", Long.class);
            System.out.println(createTime);
        });
        Assert.assertNotNull(result);
    }

    @Test
    public void testBulkRecoverPrepayDetail() {

        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());
        ServiceContext serviceContext = getServiceContextNew();

        StandardBulkRecoverAction.Arg prepayRecoverArg = new StandardBulkRecoverAction.Arg();
        prepayRecoverArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
        List<String> prepayIds = Lists.newArrayList("5a0fa652422c901dd0152570");

        prepayRecoverArg.setIdList(prepayIds);
        StandardBulkRecoverAction.Result result = this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkRecover.name(), prepayRecoverArg);
    }

    @Test
    public void testEditPrepayDetail() {
        //        {
        //            System.setProperty("spring.profiles.active", "ceshi112");
        //        }

        log.info("sysProperty:{}", System.getProperty("spring.profiles.active"));
        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("7").build());
        ServiceContext serviceContext = newServiceContext();
        String id = "5a684fcb830bdba04cd9505b";
        QueryResult<IObjectData> queryResult = prepayDetailManager.queryByField(serviceContext.getUser(), PrepayDetailConstants.API_NAME, SystemConstants.Field.Id.apiName, id, 0, 2);
        List<IObjectData> prepayDatas = queryResult.getData();

        StandardEditAction.Arg editArg = new StandardEditAction.Arg();
        editArg.setObjectData(ObjectDataDocument.of(prepayDatas.get(0)));

        StandardEditAction.Result result = this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.Edit.name(), editArg);
    }

    private ServiceContext getServiceContextNew() {
        return newServiceContext();
    }

    //    @Autowired
    //    public void testByRecordType()
    //    {
    //        optionList = recordTypeService.findByObjectDescribeApiName("55732", "CustomerAccountObj");
    //
    //    }

    @Test
    public void getByRefundId() throws Exception {
        ServiceContext serviceContext = getServiceContextNew();
        GetByRefundIdModel.Arg arg = new GetByRefundIdModel.Arg();
        arg.setRefundId("");
        GetByRefundIdModel.Result result = prepayDetailService.getByRefundId(arg, serviceContext);
        Assert.assertNotNull(result);
        log.info("getByRefundId->result:{}", result);

    }

    @Test
    public void getByPaymentId() throws Exception {
        ServiceContext serviceContext = getServiceContextNew();
        GetByPaymentIdModel.Arg arg = new GetByPaymentIdModel.Arg();
        arg.setPaymentId("cc8080a085ac4eaaa7acc2bcbe064f89");
        GetByPaymentIdModel.Result result = prepayDetailService.getByPaymentId(arg, serviceContext);
        Assert.assertNotNull("getByPaymentId->result=" + result);
        log.info("getByPaymentId->result:{}", result);
    }

    @Test
    public void testUpdateStatusToInvalid() {
        // 传入的参数，describeId和tenantId都不能为空<br>
        ServiceContext serviceContext = getServiceContextNew();
        FlowCompleteModel.Arg arg = new FlowCompleteModel.Arg();
        arg.setApprovalType(ApprovalFlowTriggerType.INVALID.getId());//invalid
        arg.setLifeStatus(SystemConstants.LifeStatus.Invalid.value);
        // paymentIdarg.setId("7d15e877abcd47b8ac5d715173d60aa0");
        arg.setObjectApiName(SystemConstants.PaymentApiName);

        arg.setDataId("7d15e877abcd47b8ac5d715173d60aa0");

        //		arg.setId("59fbfa78e567bf22dcc901ab");
        //		arg.setObjectApiName(PrepayDetailConstants.API_NAME);
        FlowCompleteModel.Result result = null;

        Assert.assertNotNull("result=" + result);
    }

    @Test
    public void testUpdateStatusToNormal() {
        // 传入的参数，describeId和tenantId都不能为空<br>
        ServiceContext serviceContext = getServiceContextNew();
        FlowCompleteModel.Arg arg = new FlowCompleteModel.Arg();
        arg.setApprovalType(ApprovalFlowTriggerType.CREATE.getId());//invalid
        arg.setLifeStatus(SystemConstants.LifeStatus.Normal.value);
        // paymentIdarg.setId("7d15e877abcd47b8ac5d715173d60aa0");
        arg.setObjectApiName(SystemConstants.PaymentApiName);

        arg.setDataId("7d15e877abcd47b8ac5d715173d60aa0");

        //		arg.setId("59fbfa78e567bf22dcc901ab");
        //		arg.setObjectApiName(PrepayDetailConstants.API_NAME);
        FlowCompleteModel.Result result = null;

        Assert.assertNotNull("result=" + result);
    }

    @Test
    public void testQuery() {
        System.out.print("nowTime=" + System.currentTimeMillis());
    }
}
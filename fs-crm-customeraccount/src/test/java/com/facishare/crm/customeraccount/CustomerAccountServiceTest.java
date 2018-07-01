package com.facishare.crm.customeraccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.GetUsedCreditAmount;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectLockAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.appframework.metadata.MetaDataService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IRecycleBinService;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/10/12.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CustomerAccountServiceTest extends BaseServiceTest {
    @Autowired
    ServiceFacade serviceFacade;
    @Autowired
    CustomerAccountService customerAccountService;
    @Autowired
    CustomerAccountManager customerAccountManager;
    @Autowired
    CrmRestApi crmRequestApi;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    IRecycleBinService recycleBinService;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    //    String tenantId = "2";
    String tenantId112 = "7";
    String tenantId55732 = "55732";
    String fsUserId = "1000";
    String customerId = "252ec102c868474e8772f89d3fac917a";
    String customerId1101 = "51324dbe7c464590a10d02dd4b72c156";
    String customerIdLock = "9e4842aaa8054386aedbd2513c53e5e1";
    String customerIdTestForInvalid = "05aef806e1e140a58a24af1fb67e8aa9";

    public CustomerAccountServiceTest() {
        super(CustomerAccountConstants.API_NAME);
    }

    @Test
    public void testAddRemindRecord() {
        Boolean b = customerAccountManager.addRemindRecord("59769", "1000", 100);
        Assert.assertNotNull(b);

    }

    @Test
    public void testQueryInvalidTest() {
        User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
        Map<String, IObjectDescribe> objectDescribeMap1 = serviceFacade.findObjects(tenantId, Lists.newArrayList(CustomerAccountConstants.API_NAME));
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(10);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, CustomerAccountConstants.Field.Customer.apiName, customerId);
        searchTemplateQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQueryWithDeleted(user, objectDescribeMap1.get(CustomerAccountConstants.API_NAME), searchTemplateQuery);
        IObjectData customerAccountObjectData = queryResult.getData().get(0);
        customerAccountObjectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList("1001"));
        IObjectData updated = serviceFacade.updateObjectData(user, customerAccountObjectData, true);
        System.out.println(queryResult.getData() + "" + updated);
    }

    @Test
    public void testMerge() {
        ServiceContext serviceContext = newServiceContext();
        CustomerAccountType.MergeCustomerArg arg = new CustomerAccountType.MergeCustomerArg();
        arg.setMainCustomerId("f541ee1dae9a487f8b7bc4df29c31495");
        arg.setSourceCustomerIds(Lists.newArrayList("451196d07be044b9b0aca1d4851791da"));
        CustomerAccountType.MergeCustomerResult result = customerAccountService.merge(serviceContext, arg);
    }

    @Test
    public void testMerge1() {
        ServiceContext serviceContext = newServiceContext();
        CustomerAccountType.MergeCustomerArg arg = new CustomerAccountType.MergeCustomerArg();
        arg.setMainCustomerId("a212165aad73429bb9d0fd18191ac7b7");
        arg.setSourceCustomerIds(Lists.newArrayList("19f67383cc914ae6b1976bbf7482cd44"));
        CustomerAccountType.MergeCustomerResult result = customerAccountService.merge(serviceContext, arg);
    }

    @Test
    public void testEnableCustomerAccount() {
        RequestContext requestContext = RequestContext.builder().tenantId("71567").user(java.util.Optional.of(new User("71567", fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        customerAccountService.enableCustomerAccount(serviceContext);

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            log.info("=====Sleep exception======");

            e.printStackTrace();
        }
        log.info("=====test end======");
    }

    @Test
    public void getByCustomerId() {
        customerAccountManager.getCustomerAccountByCustomerId(new User("55732", "1000"), "81721aa7923d4ca9a70da92d32654747");
    }

    @Test
    public void getUsedCreditAmount() {

        Map<String, String> headers = customerAccountManager.getHeaders(tenantId55732, fsUserId);
        GetUsedCreditAmount.Arg arg = new GetUsedCreditAmount.Arg();
        arg.setCustomerID("5a051f5020df9c1fcacd6b3d");
        GetUsedCreditAmount.Result result = crmRequestApi.getUsedCreditAmount(arg, headers);
        log.info("getUsedCreditAmount->result:{}", result);
    }

    /**
     *2017年11月21日 测试通过<br>
     */
    @Test
    public void testLockCustomerAccount1() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        BaseObjectLockAction.Arg lockCustomerAccountArg = new BaseObjectLockAction.Arg();
        lockCustomerAccountArg.setDataIds(Lists.newArrayList("5a0d808d422c903d44859097"));
        CustomerAccountType.LockCustomerAccountArg lockCustomerAccountArg1 = new CustomerAccountType.LockCustomerAccountArg();
        lockCustomerAccountArg1.setCustomerId(customerIdLock);
        customerAccountService.lockCustomerAccount(lockCustomerAccountArg1, serviceContext);

    }

    /**
     * 2017年11月21日 测试通过
     */
    @Test
    public void testunLockCustomerAccount1() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        BaseObjectLockAction.Arg lockCustomerAccountArg = new BaseObjectLockAction.Arg();
        lockCustomerAccountArg.setDataIds(Lists.newArrayList("5a0d808d422c903d44859097"));

        CustomerAccountType.UnlockCustomerAccountArg unlockCustomerAccountArg1 = new CustomerAccountType.UnlockCustomerAccountArg();
        unlockCustomerAccountArg1.setCustomerId("5a0d808d422c903d44859097");
        customerAccountService.unlockCustomerAccount(unlockCustomerAccountArg1, serviceContext);
    }

    /**
     * 2017-10-13 10:59:34 测试通过<Br>
     *  2017-11-01 测试通过<br>
     */
    @Test
    public void testGetCustomerAccountByPrepayBalance() {

        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        IFilter filter = new Filter();
        filter.setFieldName("prepay_balance");
        filter.setFieldValues(Lists.newArrayList("200.00"));
        filter.setOperator(Operator.EQ);

        List<IObjectData> dataList = serviceFacade.findDataWithWhere(serviceContext.getUser(), CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, 10);
        if (CollectionUtils.empty(dataList)) {
            String message = "没有查询到记录。";
            log.info(message);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_ACCOUNT_NOT_EXIST, message);
        }
        log.info("datalist= " + dataList.get(0).toJsonString());
    }

    /**
     * 2017年10月13日10:59:44 测试通过
     * optionPrepay
     */
    @Test
    public void testGetCustomerAccountByOptionPrepay() {

        RequestContext requestContext = RequestContext.builder().tenantId("2").user(java.util.Optional.of(new User("2", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        IFilter filter = new Filter();
        filter.setFieldName(CustomerAccountConstants.Field.SettleType.getApiName());
        filter.setFieldValues(Lists.newArrayList("optionPrepay"));
        filter.setOperator(Operator.EQ);
        //filter.setConnector(Where.CONN.OR.toString());

        List<IObjectData> dataList = serviceFacade.findDataWithWhere(serviceContext.getUser(), CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, 10);
        if (CollectionUtils.empty(dataList)) {
            String message = "没有查询到记录。";
            log.info(message);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CUSTOMER_ACCOUNT_NOT_EXIST, message);
        }
        log.info("datalist= " + dataList.get(0).toJsonString());
    }

    /**
     * 2017年10月13日16:52:10测试通过<br>
     */
    @Test
    public void testsBalanceAndCreditEnough() {

        ServiceContext serviceContext = newServiceContext();
        CustomerAccountType.OrderArg orderArg = new CustomerAccountType.OrderArg();
        orderArg.setCustomerId("54713d56249c484e85193ef53d14809d");
        orderArg.setOrderAmount(0.01);
        orderArg.setOldOrderAmount(0);
        orderArg.setSettleType(SettleTypeEnum.Prepay.getValue());
        CustomerAccountType.BalanceCreditEnoughResult rs = customerAccountService.isBalanceAndCreditEnough(serviceContext, orderArg);

        log.info("balanceCreditEnoughresult:{}", rs);
    }

    /**
     * 2017-10-13 17:08:51测试通过<br>
     */
    @Test
    public void testCanInvalidCustomerIds() {
        String tenantId58743 = "58743";
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId58743).user(java.util.Optional.of(new User(tenantId58743, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.CanInvalidByCustomerIdsArg canInvalidByCustomerIdsArg = new CustomerAccountType.CanInvalidByCustomerIdsArg();
        canInvalidByCustomerIdsArg.setCustomerIds(Lists.newArrayList("1f3cd2aaafcd46398ab9912617ee104e"));

        CustomerAccountType.CanInvalidByCustomerIdsResult canInvalidByCustomerIdsResult = customerAccountService.canInvalidByCustomerIds(serviceContext, canInvalidByCustomerIdsArg);
        log.info("testCanInvalidCustomerIds->canInvalidByCustomerIdsResult:{}", canInvalidByCustomerIdsResult);

    }

    @Test
    public void testBalanceEnough() {
        RequestContext requestContext = RequestContext.builder().tenantId("2").user(java.util.Optional.of(new User("2", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.PaymentArg paymentArg = new CustomerAccountType.PaymentArg();
        paymentArg.setCustomerId(customerId);
        paymentArg.setPrepayToPay(20);
        paymentArg.setRebateToPay(80);

        CustomerAccountType.BalanceEnoughResult balanceEnoughResult = customerAccountService.isBalanceEnough(serviceContext, paymentArg);
        log.info("testBalanceEnough->balanceEnoughResult:{}", balanceEnoughResult);
    }

    @Test
    public void testQUeryCustomerIdWithDeleted() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        IObjectData objectData = customerAccountManager.getDeletedObjByField(serviceContext.getUser(), CustomerAccountConstants.API_NAME, CustomerAccountConstants.Field.Customer.apiName, customerIdTestForInvalid);
        log.info("customerAccount={}", objectData);
    }

    @Test
    public void getCustomerAccountByCustomerId() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.GetByCustomerIdArg arg = new CustomerAccountType.GetByCustomerIdArg();
        arg.setCustomerId("eebe39d4fca743ed80802825279353f8");//59e97476bc5e9429c8991d1d
        Map<String, Object> objectData = customerAccountService.getByCustomerId(serviceContext, arg).getObjectData();
        System.out.println("objectData===" + objectData);
    }

    /**
     * 2017年11月21日测试OK
     */
    @Test
    public void testBulkDelteCustomerAccount() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        StandardBulkDeleteAction.Arg arg = new StandardBulkDeleteAction.Arg();
        //CustomerAccountId:
        arg.setIdList(Lists.newArrayList("5a0d808d422c903d44859097"));

        CustomerAccountType.BulkDeleteCustomerAccountArg bulkDeleteCustomerAccountArg = new CustomerAccountType.BulkDeleteCustomerAccountArg();
        bulkDeleteCustomerAccountArg.setCustomerIds(Lists.newArrayList("5a0d808d422c903d44859097"));
        customerAccountService.bulkDeleteCustomerAccount(serviceContext, bulkDeleteCustomerAccountArg);
    }

    /**
     * 2017年11月21日测试通过<br>
     */
    @Test
    public void testBulkInvalidCustomerAccount() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();

        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        SfaBulkInvalidModel.Arg sfaBulkInvalidModelArg = new SfaBulkInvalidModel.Arg();
        sfaBulkInvalidModelArg.setDataIds(Lists.newArrayList(customerIdTestForInvalid));
        sfaBulkInvalidModelArg.setLifeStatus("in_change");

        customerAccountService.bulkInvalidCustomerAccount(serviceContext, sfaBulkInvalidModelArg);
    }

    @Test
    public void testIsBalanceEnough() {
        //a61724ad778647ac8fc2f3b29d215cfa
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();

        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.PaymentArg paymentArg = new CustomerAccountType.PaymentArg();
        paymentArg.setCustomerId("a61724ad778647ac8fc2f3b29d215cfa");
        paymentArg.setRebateToPay(100);

        CustomerAccountType.BalanceEnoughResult balanceEnoughResult = customerAccountService.isBalanceEnough(serviceContext, paymentArg);
        log.info("balanceEnoughResult:{}", balanceEnoughResult);
    }

    /**
     * 测试解决客户账户编辑的问题异常的问题<br>
     */
    @Test
    public void testQueryByDataIds() {
        {
            System.setProperty("spring.profiles.active", "ceshi112");
        }

        List<String> idList = Lists.newArrayList("5a1b7b41a5083d88618436de");
        List<IObjectData> masterDataList = metaDataService.findObjectDataByIds("7", idList, CustomerAccountConstants.API_NAME);
        HashMap detailIdToMasterIdToMap = Maps.newHashMap();
        Set masterIdToCheckSet = (Set) masterDataList.stream().map((it) -> {
            String masterId = (String) it.get("AccountObj", String.class);
            detailIdToMasterIdToMap.put(it.getId(), masterId);
            log.info("testQueryByDataIds->id:{},masterid:{}", it.getId(), masterId);
            return masterId;
        }).collect(Collectors.toSet());
    }

    /**
     * 2017年11月22日 测试成功<br>
     */
    @Test
    public void testBulkRecoverCustomerAccount() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();

        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.BulkRecoverCustomerAccountArg arg = new CustomerAccountType.BulkRecoverCustomerAccountArg();
        arg.setCustomerIds(Lists.newArrayList(customerIdTestForInvalid));
        customerAccountService.bulkRecover(serviceContext, arg);
    }

    @Test
    public void testGetCustomerAccountAndCredit() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CustomerAccountType.GetCustomerAccountAndCreditInfoArg arg = new CustomerAccountType.GetCustomerAccountAndCreditInfoArg();
        arg.setCustomerId("a705703a45fd4f338fc393650a9a0fdf");
        CustomerAccountType.GetCustomerAccountAndCreditInfoResult getCustomerAccountAndCreditInfoResult = customerAccountService.getCustomerAccountAndCreditInfo(serviceContext, arg);
        log.info("getCustomerAccountAndCreditInfoResult:{}", getCustomerAccountAndCreditInfoResult);
    }

    @Test
    public void testCreateCustomerAccount() {
        System.setProperty("spring.profiles.active", "ceshi113");
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CustomerAccountType.CreateCustomerAccountArg createCustomerAccountArg = new CustomerAccountType.CreateCustomerAccountArg();
        createCustomerAccountArg.setCustomerId("ec968c718975465788993f252a968596");
        createCustomerAccountArg.setLifeStatus("under_review");
        CustomerAccountType.CreateCustomerAccountResult createCustomerAccountResult = customerAccountService.createCustomerAccount(serviceContext, createCustomerAccountArg);
        log.error("createCustomerAccountResult:{}", createCustomerAccountResult);
    }

    @Test
    public void testIsCustomerAccountEnable() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId112).user(java.util.Optional.of(new User(tenantId112, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CustomerAccountType.IsCustomerAccountEnableResult isCustomerAccountEnableResult = customerAccountService.isCustomerAccountEnable(serviceContext);
        log.info("isCustomerAccountEnable->result:{}", isCustomerAccountEnableResult);
    }

    @Test
    public void testUpdatePrepayBalance() {
        String customerid = "0deef40f9bc04f07834e81e74a3a422c";
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        String infoFormat = "id:%s - oldLifeStaus:%s - newLifeStatus:%s ";

        String info = String.format(infoFormat, 5001, 123, 345);
        String prepayId = "";
        customerAccountManager.updatePrepayBalance(serviceContext.getUser(), customerid, new BigDecimal("-3000"), null, String.valueOf(5001), prepayId);
    }

    @Test
    public void testEnableCredit() {
        {
            System.setProperty("spring.profiles.active", "ceshi112");
        }
        CustomerAccountType.UpdateCreditSwitchArg updateCreditSwitchArg = new CustomerAccountType.UpdateCreditSwitchArg();
        updateCreditSwitchArg.setSwitchType(0);
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId112).user(java.util.Optional.of(new User(tenantId112, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CustomerAccountType.EnableCreditResult enableCreditResult = customerAccountService.updateCreditSwitch(serviceContext, updateCreditSwitchArg);
        log.info("EnableCreditResult-112->result:{}", enableCreditResult);
    }

    @Test
    public void testEnableCredit113() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId55732).user(java.util.Optional.of(new User(tenantId55732, fsUserId))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CustomerAccountType.UpdateCreditSwitchArg updateCreditSwitchArg = new CustomerAccountType.UpdateCreditSwitchArg();
        updateCreditSwitchArg.setSwitchType(0);
        CustomerAccountType.EnableCreditResult enableCreditResult = customerAccountService.updateCreditSwitch(serviceContext, updateCreditSwitchArg);
        log.info("EnableCreditResult->result:{}", enableCreditResult);
    }
}

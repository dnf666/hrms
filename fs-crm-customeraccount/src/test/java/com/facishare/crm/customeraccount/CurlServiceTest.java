package com.facishare.crm.customeraccount;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseTest;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.service.CurlService;
import com.facishare.crm.customeraccount.predefine.service.dto.CurlModel;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.predefine.service.impl.CurlServiceImpl;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class CurlServiceTest extends BaseTest {
    @Autowired
    private CurlService curlService;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CustomerAccountManager customerAccountManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void selectOneTest() {
        RequestContext requestContext = RequestContext.builder().tenantId("55910").user(java.util.Optional.of(new User("55910", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.TenantIds tenantIds = new CurlModel.TenantIds();
        tenantIds.setTenantIds(Lists.newArrayList());
        EmptyResult emptyResult = curlService.updateSelectOneFieldDescribe(tenantIds, serviceContext);
        System.out.println(emptyResult);
    }

    @Test
    public void layoutTest() throws MetadataServiceException {
        RequestContext requestContext = RequestContext.builder().tenantId("7").user(java.util.Optional.of(new User("7", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.UpdateLayoutArg arg = new CurlModel.UpdateLayoutArg();
        arg.setLayoutApiName(RebateOutcomeDetailConstants.LIST_LAYOUT_API_NAME);
        EmptyResult result = curlService.updateLayout(arg, serviceContext);
        System.out.println(result);
    }

    /**
     * 2018年1月30日16:38:07 测试了，在数据库中查看了结果正常。<br>
     */
    @Test
    public void testAddOrderPaymentField() {
        String tenantId = "55910";
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.AddOrderPaymentFieldArg arg = new CurlModel.AddOrderPaymentFieldArg();
        arg.setTenantId(tenantId);
        CurlModel.AddOrderPaymentFieldResult result = curlService.addOrderPaymentField(arg, serviceContext);
        System.out.println("testAddOrderPaymentField.Result=" + result);
    }

    @Test
    public void testAddPaymentField() {
        String tenantId = "55910";
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.AddOrderPaymentFieldArg arg = new CurlModel.AddOrderPaymentFieldArg();
        arg.setTenantId(tenantId);
        CurlModel.AddOrderPaymentFieldResult result = curlService.addPaymentField(arg, serviceContext);
        System.out.println("testAddOrderPaymentField.Result=" + result);
    }

    @Test
    public void fixLifeStatusTest() {
        RequestContext requestContext = RequestContext.builder().tenantId("7").user(java.util.Optional.of(new User("7", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.FixCustomerAccountLifeStatusArg arg = new CurlModel.FixCustomerAccountLifeStatusArg();
        arg.setCustomerIds(Lists.newArrayList());
        CurlModel.FixCustomerAccountLifeStatusResult result = curlService.fixCustomerAccountLifeStatus(arg, serviceContext);
        System.out.println(result);
    }

    @Test
    public void updateTest() {
        String customerId = "42ccc02518244bdbaab3b9937fa358ab";
        Optional<IObjectData> objectDataOptional = customerAccountManager.getCustomerAccountIncludeInvalidByCustomerId(new User("7", "1000"), customerId);
        boolean deleted = objectDataOptional.get().isDeleted();
        if (deleted) {
            List<IObjectData> objectDataList = serviceFacade.bulkRecover(Lists.newArrayList(objectDataOptional.get()), new User("7", "1000"));
            System.out.print(objectDataList);
        }
    }

    @Test
    public void fixRebateIncomeStartEndTimeLabelAndTransactionTime() {
        CurlModel.TenantIds tenantIds = new CurlModel.TenantIds();
        tenantIds.setTenantIds(Lists.newArrayList("70185"));
        EmptyResult emptyResult = curlService.fixRebateIncomeStartEndTimeLabelAndTransactionTime(tenantIds, null);
        System.out.println(emptyResult);
    }

    @Test
    public void delImportFunctionPrivilege() {
        CurlModel.DelImportPrivilegeArg delImportPrivilegeArg = new CurlModel.DelImportPrivilegeArg();
        delImportPrivilegeArg.setTenantIds("55910");
        curlService.delImportFunctionPrivilege(delImportPrivilegeArg);
    }

    /**
     * 2018-03-12测试通过<br>
     */
    @Test
    public void delImportFunctionPrivilegeToRole() {
        CurlModel.AddImportPrivilegeArg addImportPrivilegeArg = new CurlModel.AddImportPrivilegeArg();
        addImportPrivilegeArg.setTenantIds("55910");
        curlService.delImportFunctionPrivilegeToRole(addImportPrivilegeArg);
    }

    /**
     * 2018-03-12测试通过<br>
     */
    @Test
    public void addImportFunctionPrivilege() {
        CurlModel.AddImportPrivilegeArg addImportPrivilegeArg = new CurlModel.AddImportPrivilegeArg();
        addImportPrivilegeArg.setTenantIds("55910");
        curlService.addImportFunctionPrivilegeToRole(addImportPrivilegeArg);
    }

    @Test
    public void initRebateUseRuleTest() {
        CurlServiceImpl.TenantIdModel.Arg arg = new CurlServiceImpl.TenantIdModel.Arg();
        arg.setTenantIds(Lists.newArrayList("2"));
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        curlService.initRebateUseRule(arg, serviceContext);
        curlService.addSalesOrderAndRebateUseRuleField(arg, serviceContext);
    }

    @Test
    public void fillRebateIncomeTypeNullTest() {
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        CurlModel.RebateIncomeIdArg arg = new CurlModel.RebateIncomeIdArg();
        arg.setRebateIncomeIds(Lists.newArrayList("5ac2ef0b7cfed9e3b39ad770"));
        curlService.fixRebateAmountByRebateIncomeTypeNull(arg, serviceContext);

    }
}

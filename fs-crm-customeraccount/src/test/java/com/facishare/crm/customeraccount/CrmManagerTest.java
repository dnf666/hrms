package com.facishare.crm.customeraccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseTest;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.job.EnableCustomerAccountJob;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.rest.dto.QueryCustomersByPage;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.ActionContext;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.dispatcher.ObjectDataProxy;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CrmManagerTest extends BaseTest {
    @Autowired
    private CrmManager crmManager;
    @Autowired
    private ObjectDataProxy objectDataProxy;
    @Autowired
    private ServiceFacade serviceFacade;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Test
    public void queryCustomersInPg() {
        //        List<String> customerIds = Lists.newArrayList("fb5d8e8834e6433f83f5138fd0972055", "eb3598e722334f408b72851c59114f2f");
        //        List<IObjectData> list = crmManager.listPlainCustomersFromPg(user, customerIds, 0, 10);
        //
        //        Map<String, Integer> customerOldStatusMap = crmManager.listCustomerStatusBeforeInvalid(user, customerIds);
        List<IObjectData> list = crmManager.listPlainCustomersFromPg(user, null, 0, 10);
        System.out.println(list);
    }

    @Test
    public void listTenantIdsOfLackCustomerAccountDatas() {
        List<String> tenantIds = crmManager.listTenantIdsOfLackCustomerAccountDatas(Lists.newArrayList("7"));
        System.out.println(tenantIds);
    }

    @Test
    public void queryTest() throws MetadataServiceException {
        ActionContext actionContext = new ActionContext();
        actionContext.setEnterpriseId(user.getTenantId());
        actionContext.setUserId(user.getTenantId());
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(100);
        QueryResult<IObjectData> result = objectDataProxy.findBySearchQuery(user.getTenantId(), SystemConstants.AccountApiName, searchTemplateQuery, actionContext);
        System.out.println();
    }

    @Test
    public void getUsedCredit() {
        ActionContext actionContext = new ActionContext();
        actionContext.setEnterpriseId(user.getTenantId());
        actionContext.setUserId(user.getTenantId());
        String customerId = "ad71b92b6f3b4efa956d4e6ca0c3b624";
        BigDecimal creditAmount = crmManager.getUsedCreditAmount(user, customerId);
        log.info("creditAmount====:{}", creditAmount);
    }

    @Test
    public void queryTest1() {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(10);
        searchTemplateQuery.setFilters(Lists.newArrayList());

        searchTemplateQuery.setWheres(new ArrayList<>());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, SystemConstants.AccountApiName, searchTemplateQuery);
        System.out.println();

    }

    @Test
    public void queryTest2() {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(10);
        IFilter filter = new Filter();
        searchTemplateQuery.setFilters(Lists.newArrayList(filter));

        searchTemplateQuery.setWheres(new ArrayList<>());
        System.out.println();

    }

    @Test
    public void test() throws Exception {
        QueryCustomersByPage.Result result = crmManager.queryCustomers(user, 0, 100);
        System.out.println(result);

        EnableCustomerAccountJob job = new EnableCustomerAccountJob();
        job.execute(null);
    }

    @Test
    public void queryInvalidCustomerTest() {
        ActionContext actionContext = new ActionContext();
        actionContext.setEnterpriseId(user.getTenantId());
        actionContext.setUserId(user.getTenantId());
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        //        IFilter filter = new Filter();
        //        filter.setOperator(Operator.EQ);
        //        filter.setFieldName("Status");
        //        filter.setFieldValues(Lists.newArrayList("99"));

        IFilter idFileter = new Filter();
        idFileter.setOperator(Operator.EQ);
        idFileter.setFieldName("_id");
        idFileter.setFieldValues(Lists.newArrayList("2c22b951f84647d88160dd7aaeb75b05"));
        //        filters.add(filter);
        filters.add(idFileter);
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(10);
        try {
            QueryResult<IObjectData> result = objectDataProxy.findBySearchQuery(user.getTenantId(), SystemConstants.PaymentApiName, searchTemplateQuery, actionContext);
            System.out.print(result);
        } catch (MetadataServiceException e) {
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.METADATA_QUERY_ERROR, e.getMessage());
        }
    }
}

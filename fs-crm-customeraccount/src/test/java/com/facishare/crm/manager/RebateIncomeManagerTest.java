package com.facishare.crm.manager;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateIncomeManagerTest {
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    private ServiceFacade serviceFacade;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }
    String tenantId = "55732";
    String fsUserId = "1000";
    String customerId = "eebe39d4fca743ed80802825279353f8";

    @Test
    public void queryCustomerIdsTest() {
    }

    @Test
    public void queryRecoverTest() {
        List<IObjectData> rebateIncomeDatas = serviceFacade.findObjectDataByIds("55732", Lists.newArrayList("5a129bc9bab09c0d5d3cbe2e"), RebateIncomeDetailConstants.API_NAME);
        System.out.println(rebateIncomeDatas);
    }

    @Test
    public void queryInvalidDataTest() {
        QueryResult<IObjectData> result = rebateIncomeDetailManager.queryInvalidDataByField(new User(tenantId, fsUserId), RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Customer.apiName, Lists.newArrayList("0b23893d0d7b477386713d8d997bbbf9"), 0, 10);
        System.out.println(result);
    }

    @Test
    public void queryTest1() {
        List<IObjectData> result = rebateIncomeDetailManager.listByRefundIds(new User(tenantId, fsUserId), Lists.newArrayList("09c1a1a9a57f430ba413b12b19b99abb"));
        System.out.println(result);
    }

    @Test
    public void listInvalidDataByRefundIdsTest() {
        List<IObjectData> list = rebateIncomeDetailManager.listInvalidDataByIds(new User("59768", "1000"), Lists.newArrayList("5a3721b3a5083d051fda9730"));
        System.out.println(list);
    }

    @Test
    public void queryTest() {
        User user = new User(tenantId, fsUserId);
        //结束时间过滤器
        IFilter availableRebateFilter = new Filter();
        availableRebateFilter.setOperator(Operator.GT);
        availableRebateFilter.setFieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        availableRebateFilter.setFieldValues(Lists.newArrayList("0"));

        //LifeStatus正常状态
        IFilter lifeStatusFilter = new Filter();
        lifeStatusFilter.setOperator(Operator.EQ);
        lifeStatusFilter.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        lifeStatusFilter.setFieldValues(Lists.newArrayList(SystemConstants.LifeStatus.Normal.value));

        IFilter startDateFilter = new Filter();
        startDateFilter.setOperator(Operator.LTE);
        startDateFilter.setFieldName(RebateIncomeDetailConstants.Field.StartTime.apiName);
        startDateFilter.setFieldValues(Lists.newArrayList(String.valueOf(System.currentTimeMillis())));

        IFilter endDateFilter = new Filter();
        endDateFilter.setOperator(Operator.GT);
        endDateFilter.setFieldName(RebateIncomeDetailConstants.Field.EndTime.apiName);
        endDateFilter.setFieldValues(Lists.newArrayList(String.valueOf(System.currentTimeMillis())));

        List<IFilter> filterList = Lists.newArrayList(lifeStatusFilter, endDateFilter, startDateFilter);
        QueryResult<IObjectData> result = rebateIncomeDetailManager.queryByFieldFilterList(user, RebateIncomeDetailConstants.API_NAME, filterList, 0, 100);

        IObjectData data = rebateIncomeDetailManager.getDeletedObjByField(user, RebateIncomeDetailConstants.API_NAME, ObjectData.ID, "0b23893d0d7b477386713d8d997bbbf9");
        System.out.println(data);
    }

    @Test
    public void updateTest() {
        User user = new User(tenantId, fsUserId);
        IObjectData data = rebateIncomeDetailManager.getDeletedObjByField(user, RebateIncomeDetailConstants.API_NAME, ObjectData.ID, "5a1bfd8abab09c6a16f314cb");

        Object object = rebateIncomeDetailManager.updateTmp(user, data, "create_time");
        System.out.println(object);
    }
}

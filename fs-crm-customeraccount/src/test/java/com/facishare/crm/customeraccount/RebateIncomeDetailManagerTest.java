package com.facishare.crm.customeraccount;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateIncomeModle.PayForOutcomeModel;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class RebateIncomeDetailManagerTest extends BaseActionTest {
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;

    @Autowired
    private ServiceFacade serviceFacade;

    public RebateIncomeDetailManagerTest() {
        super(RebateIncomeDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void obtainRebateIncomeToPayList() {
        String customerId = "096519a596d44173aeadccf826108c4c";
        BigDecimal totalAmountToPay = new BigDecimal("3.52");
        List<PayForOutcomeModel> list = rebateIncomeDetailManager.obtainRebateIncomeToPayList(user, totalAmountToPay, customerId);
        System.out.println(list);
    }

    @Test
    public void testDeleted() {
        IObjectData objectData = rebateIncomeDetailManager.getDeletedObjByField(user, apiName, ObjectData.ID, "5a1d02d2bab09cb684abedca");
        System.out.println(objectData);
    }

    @Test
    public void listNowDayEnableRebateIncomeDetailsTest() {
        List<IObjectData> list = rebateIncomeDetailManager.listNowDayEnableRebateIncomeDetails(tenantId, 0, 10);
        System.out.println(list);
    }

    @Test
    public void queryTest() {
        QueryResult<IObjectData> queryResult = rebateIncomeDetailManager.queryInvalidDataByField(new User("55732", "1000"), RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Customer.apiName, Lists.newArrayList("0b23893d0d7b477386713d8d997bbbf9"), 0, 10);
        log.info("queryResult:====>{}", queryResult);
        for (IObjectData objectData : queryResult.getData()) {
            if ("invalid".equals(objectData.get(SystemConstants.Field.LifeStatus.apiName))) {
                log.info("invalid objectdata:{}", objectData);
            }
        }
    }

    @Test
    public void queryByServiceFacade() {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(0);
        searchTemplateQuery.setLimit(10000);

        //        IFilter filter = new Filter();
        //        String apiName = SystemConstants.Field.TennantID.apiName;
        //        filter.setFieldName(apiName);
        //        filter.setFieldValues(Lists.newArrayList(tenantId));
        //        filter.setOperator(Operator.EQ);
        //
        //        searchTemplateQuery.setFilters(Lists.newArrayList(filter));

        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(new User("55732", "1000"), RebateIncomeDetailConstants.API_NAME, searchTemplateQuery);

        for (IObjectData objectData : queryResult.getData()) {
            if ("invalid".equals(objectData.get(SystemConstants.Field.LifeStatus.apiName))) {
                log.info("invalid objectdata:{}", objectData);
            }
        }
    }

}

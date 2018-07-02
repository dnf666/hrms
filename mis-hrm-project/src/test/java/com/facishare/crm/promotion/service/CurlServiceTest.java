package com.facishare.crm.promotion.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.base.BaseServiceTest;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.predefine.service.CurlService;
import com.facishare.crm.promotion.predefine.service.dto.AddEditActionModel;
import com.facishare.crm.promotion.predefine.service.dto.EmptyResult;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class CurlServiceTest extends BaseServiceTest {
    @Autowired
    private CurlService curlService;

    public CurlServiceTest() {
        super(PromotionConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void addEditActionTest() {
        AddEditActionModel.Arg apiNameArg = new AddEditActionModel.Arg();
        apiNameArg.setObjectApiNames(Lists.newArrayList(PromotionProductConstants.API_NAME, PromotionRuleConstants.API_NAME));
        EmptyResult emptyResult = curlService.addEidtAction(newServiceContext(), apiNameArg);
        System.out.println(emptyResult);
    }

    @Test
    public void addGiftTypeInPromotionRuleTest() {
        CurlService.TenantIdModel.Arg arg = new CurlService.TenantIdModel.Arg();
        arg.setTenantIds(Lists.newArrayList("55910"));
        EmptyResult emptyResult = curlService.addGiftTypeInPromotionRule(arg, null);
        System.out.println(emptyResult);
    }

    @Test
    public void fillGiftTypeTest() {
        CurlService.TenantIdModel.Arg arg = new CurlService.TenantIdModel.Arg();
        arg.setTenantIds(Lists.newArrayList("2"));
        CurlService.TenantIdModel.Result result = curlService.fillGiftType(null, arg);
        System.out.println(result);
    }

    @Test
    public void initAdvertisementTest() {
        CurlService.TenantIdModel.Arg arg = new CurlService.TenantIdModel.Arg();
        arg.setTenantIds(Lists.newArrayList("55910"));
        curlService.initAdvertisement(null, arg);
    }
}

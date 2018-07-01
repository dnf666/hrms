package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.enums.type.PayTypeEnum;
import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.dao.TenantQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyQuotaType;
import com.facishare.crm.electronicsign.predefine.service.dto.TenantQuotaType;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * created by dailf on 2018/4/25
 *
 * @author dailf
 */
@Slf4j
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TenantQuotaServiceTest extends BaseServiceTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Override
    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = User.SUPPER_ADMIN_USER_ID;
    }

    @Resource
    private TenantQuotaDAO tenantQuotaDAO;
    @Resource
    private TenantQuotaService tenantQuotaService;

    public TenantQuotaServiceTest() {
        super("");
    }

    @Test
    public void getTenantQuota() {
        TenantQuotaType.GetTenantQuotaByPage.Arg param = new TenantQuotaType.GetTenantQuotaByPage.Arg();
        param.setCurrentPage(2);
        param.setPageSize(4);
        param.setQuotaType("2");
        System.out.println(tenantQuotaService.getTenantQuotaByPage(param));
        List<BuyRecordDO> list = new ArrayList<>();
        list.add(new BuyRecordDO());
    }

    @Test
    public void buyQuotaByFs() {
        BuyQuotaType.AddBuyQuota.Arg param = new BuyQuotaType.AddBuyQuota.Arg();
        param.setTenantId("1");
        param.setQuotaType("2");
        param.setBuyQuota(2);
        param.setPayType(PayTypeEnum.SYSTEM.getType());
        tenantQuotaService.buyQuotaByFs(this.newServiceContext(), param);

    }

    }

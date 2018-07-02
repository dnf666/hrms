package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.dao.BuyRecordDAO;
import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyRecordType;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/4/25
 * @author dailf
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class BuyRecordServiceTest extends BaseServiceTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }
    @Resource
    private BuyRecordService buyRecordService;
    @Resource
    private BuyRecordDAO buyRecordDAO;

    public BuyRecordServiceTest() {
        super("");
    }

    @Override
    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = "1000";

    }

    @Test
    public void queryByPageForFsManage() {
        BuyRecordType.QueryByPage.Arg buyQuotaParam = new BuyRecordType.QueryByPage.Arg();
        buyQuotaParam.setCurrentPage(1);
        buyQuotaParam.setPageSize(3);
        buyQuotaParam.setPayType("微信");
        System.out.println(buyRecordService.queryByPageForFsManage(this.newServiceContext(), buyQuotaParam));


    }

    @Test
    public void queryByPage() {
        BuyRecordType.QueryByPage.Arg buyQuotaParam = new BuyRecordType.QueryByPage.Arg();
        buyQuotaParam.setCurrentPage(1);
        buyQuotaParam.setPageSize(20);
        buyQuotaParam.setQuotaType(null);
        System.out.println(buyRecordService.queryByPage(this.newServiceContext(), buyQuotaParam));
    }


}
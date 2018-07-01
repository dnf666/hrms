package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.dao.FsQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.CertifyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.FsQuotaDO;
import com.facishare.crm.electronicsign.predefine.service.dto.FsQuotaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class FsQuotaServiceTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private FsQuotaService fsQuotaService;
    @Resource
    private FsQuotaDAO fsQuotaDAO;



    @Test
    public void getfsquota() {
        Assert.assertEquals(new Long(0l),fsQuotaService.getFsQuota().getFsQuotaVO().getSaleMoney());
    }
    @Test
    public void insert(){
        FsQuotaDO fsQuotaDO = new FsQuotaDO();
        fsQuotaDO.setSaleEnterpriseQuota(0L);
        fsQuotaDO.setSaleIndividualQuota(0L);
        fsQuotaDO.setSaleMoney(0L);
        fsQuotaDO.setCreateTime(System.currentTimeMillis());
        fsQuotaDO.setUpdateTime(System.currentTimeMillis());
        fsQuotaDAO.save(fsQuotaDO);
    }


}

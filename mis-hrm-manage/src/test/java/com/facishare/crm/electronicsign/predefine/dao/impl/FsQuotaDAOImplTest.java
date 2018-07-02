package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.FsQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.FsQuotaDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * created by dailf on 2018/4/23
 *
 * @author dailf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/test-mongo.xml")
@Slf4j
public class FsQuotaDAOImplTest {

    @Resource
    private FsQuotaDAO fsQuotaDAO;

    @Test
    public void getFsQuota() {
        FsQuotaDO fsQuotaDO = new FsQuotaDO();
        fsQuotaDO.setSaleIndividualQuota(987345L);
        fsQuotaDAO.save(fsQuotaDO);
        log.info("{}", fsQuotaDO);
        Assert.assertTrue(Objects.nonNull(fsQuotaDO.getId()));
    }

}
package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.type.CertifyTypeEnum;
import com.facishare.crm.electronicsign.predefine.dao.CertifyRecordDAO;
import com.facishare.crm.electronicsign.predefine.model.CertifyRecordDO;
import com.facishare.crm.electronicsign.predefine.service.dto.CertifyRecordType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * created by dailf on 2018/4/26
 *
 * @author dailf
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class CertifyRecordServiceTest {
    @Resource
    private CertifyRecordDAO certifyRecordDAO;
    @Resource
    private CertifyRecordService certifyRecordService;
    @Test
    public void getCertifyRecordByPage() {
        CertifyRecordType.GetCertifyRecordByPage.Arg param = new CertifyRecordType.GetCertifyRecordByPage.Arg();
        param.setCurrentPage(1);
        param.setPageSize(5);
        param.setUserObj(2);
        System.out.println(certifyRecordService.getCertifyRecordByPage(param));


    }


}
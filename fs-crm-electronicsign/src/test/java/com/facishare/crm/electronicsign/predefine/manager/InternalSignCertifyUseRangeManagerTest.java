package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import com.facishare.paas.appframework.common.service.OrgService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class InternalSignCertifyUseRangeManagerTest {
    @Resource
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager;

    @Resource
    private OrgService orgService;



    @Test
    public void getBestSignAccount_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<String> deptIds = Lists.newArrayList("3", "2", "1");

        String tenantId = "110";
        InternalSignCertifyUseRangeDO range1 = new InternalSignCertifyUseRangeDO();
        range1.setBestSignAccount("rangAccount1");
        range1.setTenantId(tenantId);
        range1.setCertifyStatus(CertifyStatusEnum.CRTTIFIED.getStatus());
        range1.setUseStatus(UseStatusEnum.ON.getStatus());
        range1.setDepartmentIds(Lists.newArrayList("1"));

        InternalSignCertifyUseRangeDO range2 = new InternalSignCertifyUseRangeDO();
        range2.setBestSignAccount("rangAccount2");
        range2.setTenantId(tenantId);
        range2.setCertifyStatus(CertifyStatusEnum.CRTTIFIED.getStatus());
        range2.setUseStatus(UseStatusEnum.UN_USE.getStatus());
        range2.setDepartmentIds(Lists.newArrayList("2", "2.1"));

        InternalSignCertifyUseRangeDO range3 = new InternalSignCertifyUseRangeDO();
        range3.setBestSignAccount("rangAccount3");
        range3.setCertifyStatus(CertifyStatusEnum.CERTIFYING.getStatus());
        range1.setUseStatus(UseStatusEnum.ON.getStatus());
        range3.setTenantId(tenantId);
        range3.setDepartmentIds(Lists.newArrayList("3.1", "3.2", "3"));

        Method getBestSignAccountMethod = InternalSignCertifyUseRangeManager.class.getDeclaredMethod("getBestSignAccount", List.class, List.class);
        getBestSignAccountMethod.setAccessible(true);
        Optional<String> bestSignAccoutnOptional = (Optional<String>) getBestSignAccountMethod.invoke(internalSignCertifyUseRangeManager, deptIds, Lists.newArrayList(range3, range2, range1));
        Assert.assertTrue(bestSignAccoutnOptional.isPresent());
        Assert.assertEquals(bestSignAccoutnOptional.get(), range1.getBestSignAccount());
    }
}

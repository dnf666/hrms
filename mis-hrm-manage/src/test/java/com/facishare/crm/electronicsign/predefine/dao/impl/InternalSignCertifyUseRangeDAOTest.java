package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.predefine.dao.InternalSignCertifyUseRangeDAO;
import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/test-mongo.xml")
@Slf4j
public class InternalSignCertifyUseRangeDAOTest {

    @Resource
    private InternalSignCertifyUseRangeDAO internalSignCertifyUseRangeDAO;

    @Test
    public void queryByTenantIdAndDeptIds_Success() {
        String tenantId = "110";

        InternalSignCertifyUseRangeDO range1 = new InternalSignCertifyUseRangeDO();
        range1.setBestSignAccount("rangAccount1");
        range1.setTenantId(tenantId);
        range1.setDepartmentIds(Lists.newArrayList("1"));
        internalSignCertifyUseRangeDAO.save(range1);

        InternalSignCertifyUseRangeDO range2 = new InternalSignCertifyUseRangeDO();
        range2.setBestSignAccount("rangAccount2");
        range2.setTenantId(tenantId);
        range2.setDepartmentIds(Lists.newArrayList("2", "1"));
        internalSignCertifyUseRangeDAO.save(range2);

        InternalSignCertifyUseRangeDO range3 = new InternalSignCertifyUseRangeDO();
        range3.setBestSignAccount("rangAccount3");
        range3.setTenantId(tenantId);
        range3.setDepartmentIds(Lists.newArrayList("3", "2", "1"));
        internalSignCertifyUseRangeDAO.save(range3);

        List<InternalSignCertifyUseRangeDO> rangeDOList = internalSignCertifyUseRangeDAO.queryByTenantIdAndDeptIds(tenantId, Lists.newArrayList("1"));
        Assert.assertTrue(rangeDOList.size() == 3);

        rangeDOList = internalSignCertifyUseRangeDAO.queryByTenantIdAndDeptIds(tenantId, Lists.newArrayList("3"));
        Assert.assertTrue(rangeDOList.size() == 1);
        Assert.assertTrue(Objects.equals(rangeDOList.get(0).getBestSignAccount(), range3.getBestSignAccount()));

        rangeDOList = internalSignCertifyUseRangeDAO.queryByTenantIdAndDeptIds(tenantId, Lists.newArrayList("2"));
        Assert.assertTrue(rangeDOList.size() == 2);
    }

    @Test
    public void updateDepartments_Success() {
        String tenantId = "asdf1234";
        String internalSignCertifyId = "internalSignCertifyId1234";
        List<String> departmentIds = Lists.newArrayList("1", "2");
        InternalSignCertifyUseRangeDO internalSignCertifyUseRangeDO = new InternalSignCertifyUseRangeDO();
        internalSignCertifyUseRangeDO.setTenantId(tenantId);
        internalSignCertifyUseRangeDO.setInternalSignCertifyId(internalSignCertifyId);
        internalSignCertifyUseRangeDO.setDepartmentIds(departmentIds);
        internalSignCertifyUseRangeDO.setBestSignAccount("asdf");
        internalSignCertifyUseRangeDO.setUseStatus(UseStatusEnum.ON.getStatus());
        internalSignCertifyUseRangeDO.setCertifyStatus(CertifyStatusEnum.CRTTIFIED.getStatus());
        internalSignCertifyUseRangeDO.setCreateTime(System.currentTimeMillis());
        internalSignCertifyUseRangeDO.setUpdateTime(System.currentTimeMillis());

        UpdateResults updateResults = this.internalSignCertifyUseRangeDAO.updateDepartments(internalSignCertifyUseRangeDO);
        Assert.assertEquals(updateResults.getInsertedCount(), 1);

        String id = updateResults.getNewId().toString();

        InternalSignCertifyUseRangeDO queryResult = internalSignCertifyUseRangeDAO.queryById(id);
        Assert.assertEquals(queryResult.getBestSignAccount(), internalSignCertifyUseRangeDO.getBestSignAccount());
        Assert.assertEquals(queryResult.getDepartmentIds(), internalSignCertifyUseRangeDO.getDepartmentIds());
        Assert.assertEquals(queryResult.getCertifyStatus(), internalSignCertifyUseRangeDO.getCertifyStatus());
        Assert.assertEquals(queryResult.getUseStatus(), internalSignCertifyUseRangeDO.getUseStatus());
        Assert.assertEquals(queryResult.getUseStatus(), internalSignCertifyUseRangeDO.getUseStatus());
        Assert.assertEquals(queryResult.getTenantId(), internalSignCertifyUseRangeDO.getTenantId());
        Assert.assertNotNull(queryResult.getCreateTime());
        Assert.assertNotNull(queryResult.getUpdateTime());
        Assert.assertEquals(queryResult.getInternalSignCertifyId(), internalSignCertifyUseRangeDO.getInternalSignCertifyId());

        internalSignCertifyUseRangeDO.setDepartmentIds(Lists.newArrayList());
        updateResults = internalSignCertifyUseRangeDAO.updateDepartments(internalSignCertifyUseRangeDO);
        Assert.assertEquals(updateResults.getUpdatedCount(), 1);
        queryResult = internalSignCertifyUseRangeDAO.queryById(id);
        Assert.assertEquals(queryResult.getBestSignAccount(), internalSignCertifyUseRangeDO.getBestSignAccount());
        Assert.assertEquals(queryResult.getDepartmentIds(), internalSignCertifyUseRangeDO.getDepartmentIds());
        Assert.assertEquals(queryResult.getCertifyStatus(), internalSignCertifyUseRangeDO.getCertifyStatus());
        Assert.assertEquals(queryResult.getUseStatus(), internalSignCertifyUseRangeDO.getUseStatus());
        Assert.assertEquals(queryResult.getUseStatus(), internalSignCertifyUseRangeDO.getUseStatus());
        Assert.assertEquals(queryResult.getTenantId(), internalSignCertifyUseRangeDO.getTenantId());
        Assert.assertNotNull(queryResult.getCreateTime());
        Assert.assertNotNull(queryResult.getUpdateTime());
        Assert.assertEquals(queryResult.getInternalSignCertifyId(), internalSignCertifyUseRangeDO.getInternalSignCertifyId());
    }

    @Test
    public void deleteByInternalSignCertifyId_Success() {
        String tenantId = "asdf1234";
        String internalSignCertifyId = "internalSignCertifyId1234";
        List<String> departmentIds = Lists.newArrayList("1", "2");
        InternalSignCertifyUseRangeDO internalSignCertifyUseRangeDO = new InternalSignCertifyUseRangeDO();
        internalSignCertifyUseRangeDO.setTenantId(tenantId);
        internalSignCertifyUseRangeDO.setInternalSignCertifyId(internalSignCertifyId);
        internalSignCertifyUseRangeDO.setDepartmentIds(departmentIds);
        internalSignCertifyUseRangeDO.setBestSignAccount("asdf");
        internalSignCertifyUseRangeDO.setUseStatus(UseStatusEnum.ON.getStatus());
        internalSignCertifyUseRangeDO.setCertifyStatus(CertifyStatusEnum.CRTTIFIED.getStatus());
        internalSignCertifyUseRangeDO.setCreateTime(System.currentTimeMillis());
        internalSignCertifyUseRangeDO.setUpdateTime(System.currentTimeMillis());

        UpdateResults updateResults = this.internalSignCertifyUseRangeDAO.updateDepartments(internalSignCertifyUseRangeDO);
        Assert.assertEquals(updateResults.getInsertedCount(), 1);

        String id = updateResults.getNewId().toString();

        InternalSignCertifyUseRangeDO queryResult = internalSignCertifyUseRangeDAO.queryById(id);
        Assert.assertTrue(Objects.nonNull(queryResult));

        internalSignCertifyUseRangeDAO.deleteByInternalSignCertifyId(internalSignCertifyId);

        queryResult = internalSignCertifyUseRangeDAO.queryById(id);
        Assert.assertTrue(Objects.isNull(queryResult));
    }
}

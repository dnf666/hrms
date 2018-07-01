package com.facishare.crm.customeraccount;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class customerAccountConfigDaoTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }
    String tenantId = "55732";

    @Autowired
    CustomerAccountConfigDao customerAccountConfigDao;

    /**
     * 2017-11-14测试成功
     */
    @Test
    public void testCustomerAccountDaoAdd() {

        CustomerAccountConfig customerAccountConfig = new CustomerAccountConfig();
        customerAccountConfig.setTenantId("2");
        customerAccountConfig.setCreateBy("xujf");
        customerAccountConfig.setCreateTime(new Date());
        customerAccountConfig.setUpdateBy("XX");
        customerAccountConfig.setUpdateTime(new Date());
        customerAccountConfig.setCreditEnable(true);

        //save 和 insert的区别。<Br>
        customerAccountConfigDao.insert(customerAccountConfig);

    }

    @Test
    public void testCustomerAccountDaoEdit() {

        CustomerAccountConfig customerAccountConfig = new CustomerAccountConfig();
        customerAccountConfig.setTenantId("55732");
        customerAccountConfig.setCreateBy("xujf");
        customerAccountConfig.setCreateTime(new Date());
        customerAccountConfig.setUpdateBy("XX");
        customerAccountConfig.setUpdateTime(new Date());
        customerAccountConfig.setCreditEnable(true);
        customerAccountConfig.setCustomerAccountEnable(CustomerAccountType.CustomerAccountEnableSwitchStatus.FAILED.getValue());

        customerAccountConfigDao.update(customerAccountConfig);

    }

    @Test
    public void testCustomerAccountDaoDelete() {

        CustomerAccountConfig customerAccountConfig = new CustomerAccountConfig();
        customerAccountConfig.setTenantId("55732");
        customerAccountConfig.setCreateBy("xujf");
        customerAccountConfig.setCreateTime(new Date());
        customerAccountConfig.setUpdateBy("XX");
        customerAccountConfig.setUpdateTime(new Date());
        customerAccountConfig.setCreditEnable(true);

        customerAccountConfigDao.delete(customerAccountConfig);

    }

    @Test
    public void testCustomerAccountQueryDelete() {
        customerAccountConfigDao.findByTenantId(tenantId);

    }

    /**
     *
     */
    @Test
    public void testFindByTenantId() {
        CustomerAccountConfig customerAccountConfig = customerAccountConfigDao.findByTenantId("55732");
        log.info("customerAccountConfig:{},tenantId:{}", customerAccountConfig, customerAccountConfig.getTenantId());
    }

    /**
     *
     */
    @Test
    public void testFindOpeningConfig() {
        List<CustomerAccountConfig> customerAccountConfig = customerAccountConfigDao.findEnterpriseWithCustomerAccountOpeningByTenantIds(Lists.newArrayList("2"));
        log.info("customerAccountConfigList:{}", customerAccountConfig);
    }

}

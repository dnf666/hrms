package com.facishare.crm.manager;

import com.facishare.crm.customeraccount.dao.CustomerAccountBillDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountBill;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountBillManager;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by xujf on 2017/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CustomerAccountBillManagerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    CustomerAccountBillManager customerAccountBillManager;

    @Autowired
    CustomerAccountBillDao customerAccountBillDao;

    /**
     * 2017-12-14测试通过<br>
     */
    @Test
    public void testMergeBill() {
        String sourceAccountId = "abc-1";
        String destAccountId = "def-1";
        customerAccountBillManager.mergeBill(Lists.newArrayList(sourceAccountId), destAccountId);
    }

    private void mockSourceData() {
        CustomerAccountBill bill1 = new CustomerAccountBill();
        bill1.setBillDate(new Date());
        bill1.setCustomerAccountId("abc-1");
        bill1.setPrepayAmountChange(2);
        bill1.setPrepayLockedAmountChange(2);
        bill1.setRebateLockedAmountChange(2);
        bill1.setRebateAmountChange(2);
        bill1.setRelateId("m1-abc-1");
        bill1.setTenantId("2");
        bill1.setCreateTime(new Date());
        bill1.setCreateBy("xjf");
        customerAccountBillDao.insert(bill1);
    }

    private void mockDestData() {
        CustomerAccountBill bill1 = new CustomerAccountBill();
        bill1.setBillDate(new Date());
        bill1.setCustomerAccountId("def-1");
        bill1.setPrepayAmountChange(3);
        bill1.setPrepayLockedAmountChange(3);
        bill1.setRebateLockedAmountChange(3);
        bill1.setRebateAmountChange(3);
        bill1.setRelateId("m1-def-1");
        bill1.setTenantId("2");
        bill1.setCreateTime(new Date());
        bill1.setCreateBy("xjf");
        customerAccountBillDao.insert(bill1);
    }

    @Test
    public void mockDataTest() {
        mockSourceData();
        mockDestData();
    }

    @Test
    public void mockDataTest2() {
        CustomerAccountBill bill1 = new CustomerAccountBill();
        bill1.setBillDate(new Date());
        bill1.setCustomerAccountId("abc-2");
        bill1.setPrepayAmountChange(2);
        bill1.setPrepayLockedAmountChange(2);
        bill1.setRebateLockedAmountChange(2);
        bill1.setRebateAmountChange(2);
        bill1.setRelateId("m1-abc-1");
        bill1.setTenantId("2");
        bill1.setCreateTime(new Date());
        bill1.setCreateBy("xjf");
        customerAccountBillDao.insert(bill1);
    }
}

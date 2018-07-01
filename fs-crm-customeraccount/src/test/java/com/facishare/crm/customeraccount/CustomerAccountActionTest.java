package com.facishare.crm.customeraccount;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class CustomerAccountActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private ServiceFacade serviceFacade;

    public CustomerAccountActionTest() {
        super(CustomerAccountConstants.API_NAME);
    }

    @Test
    public void addCustomerAccount() {
        String customerId = "581dc196593c4aac9b639b3f5b4a3d6b";
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set(CustomerAccountConstants.Field.Customer.apiName, customerId);
        objectData.set(CustomerAccountConstants.Field.SettleType.apiName, Lists.newArrayList(SettleTypeEnum.Prepay.getValue(), SettleTypeEnum.Cash.getValue()));
        objectData.set(CustomerAccountConstants.Field.CreditQuota.apiName, 100);
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        Object result = executeAdd(objectData);
        System.out.println("addCustomerAccount->result=" + result);
    }

    @Test
    public void editCustomerAccount() {
        IObjectData objectData = serviceFacade.findObjectData(user, "5a67ff25bab09cd292fe1f82", CustomerAccountConstants.API_NAME);
        objectData.set(CustomerAccountConstants.Field.SettleType.apiName, Lists.newArrayList("1"));
        objectData.set(CustomerAccountConstants.Field.CreditQuota.apiName, BigDecimal.valueOf(1));
        Object result = executeEdit(objectData);
        System.out.println("editCustomerAccount->result=" + result);
    }
}

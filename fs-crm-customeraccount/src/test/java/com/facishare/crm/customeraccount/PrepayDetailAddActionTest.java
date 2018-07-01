package com.facishare.crm.customeraccount;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.PrepayOutcomeTypeEnum;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrepayDetailAddActionTest extends BaseActionTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    protected ServiceFacade serviceFacade;

    public PrepayDetailAddActionTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    /**
     * dev_6.2下 在prepay_detail增加了 order_payment_id 的情况下 执行成功。<br>
     */

    @Test
    public void addPrepayIncomeDetailConstants() {
        String customerId = "36701b64b1344b499b56b4a47f816e3c";
        IObjectData objectData = new ObjectData();
        objectData.set(PrepayDetailConstants.Field.Customer.apiName, customerId);
        objectData.set(PrepayDetailConstants.Field.Amount.apiName, "56");

        objectData.set(PrepayDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(PrepayDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.OnlineCharge.getValue());
        //objectData.set(PrepayDetailConstants.Field.Payment.apiName, "7d15e877abcd47b8ac5d715173d60aa0");
        Object result = executeAdd(objectData);
        System.out.println("addPrepayIncomeDetailConstants=" + result);
    }

    @Test
    public void addPrepayOutcomeDetailConstants() {
        String customerId = "eebe39d4fca743ed80802825279353f8";
        IObjectData objectData = new ObjectData();
        objectData.set(PrepayDetailConstants.Field.Customer.apiName, customerId);
        objectData.set(PrepayDetailConstants.Field.Amount.apiName, "32132");
        objectData.set(PrepayDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(PrepayDetailConstants.Field.OutcomeType.apiName, PrepayOutcomeTypeEnum.OffsetOrder.getValue());
        objectData.set(PrepayDetailConstants.Field.Refund.apiName, "512a2f4ebbc544a8bcbec5eb41868ed7");
        Object result = executeAdd(objectData);
        System.out.println(result);
    }

    @Test
    public void edit() {
        String customerId = "eebe39d4fca743ed80802825279353f8";
        IObjectData objectData = new ObjectData();
        objectData.set("_id", "59fc292dbab09c4b26e99712");
        objectData.setRecordType("income_record_type__c");
        objectData.set(PrepayDetailConstants.Field.Customer.apiName, customerId);
        objectData.set(PrepayDetailConstants.Field.Amount.apiName, "32132");
        objectData.set(PrepayDetailConstants.Field.TransactionTime.apiName, new Date().getTime());
        objectData.set(PrepayDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.OnlineCharge.getValue());
        Object result = executeEdit(objectData);
        System.out.println(result);
    }

    @Test
    public void test() {
        IObjectData data = serviceFacade.findObjectData(new User(tenantId, fsUserId), "59fc3998e567bf0688bea03a", apiName);
        System.out.println(data.toJsonString());
    }

}

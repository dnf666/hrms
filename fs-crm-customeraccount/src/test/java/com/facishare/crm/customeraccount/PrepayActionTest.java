package com.facishare.crm.customeraccount;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.common.BaseActionTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PrepayActionTest extends BaseActionTest {
    public PrepayActionTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void add() {
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.setTenantId("55732");
        objectData.set("lock_user", Arrays.asList(1000));
        objectData.set("owner", Arrays.asList("1000"));
        objectData.set("name", "1");
        objectData.set("customer_id", "9b94460624da4ff694c06eacfbebea95");
        objectData.set("customer_account_id", "5a0be20ebab09ca1c21383dd");
        objectData.set("amount", 0.01);
        objectData.set("transaction_time", new DateTime());
        //objectData.set("payment_id", "124");
        //objectData.set("refund_id", "123");
        objectData.set("income_type", "1");
        //objectData.set("outcome_type", "offsetOrder");
        objectData.set("online_charge_no", "12312");
        ObjectDataDocument objectDataDocument = (ObjectDataDocument) executeAdd(objectData);
        System.out.print("----");
    }

    @Test
    public void edit() {
        IObjectData objectData = new ObjectData();
        objectData.setId("59fd6c395efa9951d43167b4");
        objectData.setRecordType("default__c");
        objectData.setTenantId("55732");
        //objectData.set("lock_user", Lists.newArrayList(1000));
        //objectData.set("owner", Lists.newArrayList("1000"));
        //objectData.set("name", "1");
        objectData.set("customer_id", "b339f6eb97c1450cb54432ea94761487");
        objectData.set("customer_account_id", "59fbe11ee567bf2774f3d2ea");
        objectData.set("amount", 120);
        objectData.set("transaction_time", new Date());
        //objectData.set("payment_id", "124");
        //objectData.set("refund_id", "123");
        //objectData.set("income_type", "1");
        //objectData.set("outcome_type", "offsetOrder");
        objectData.set("online_charge_no", "1111111111111");
        BaseObjectSaveAction.Result result = (BaseObjectSaveAction.Result) executeEdit(objectData);
    }

    @Test
    public void invalid() {
        StandardInvalidAction.Arg arg = new StandardInvalidAction.Arg();
        IObjectData objectData = new ObjectData();
        objectData.setId("59fbf98bbab09c36782004dc");
        objectData.setDescribeApiName(PrepayDetailConstants.API_NAME);
        //StandardInvalidAction.Result result = (StandardInvalidAction.Result) executeInvalid(objectData);
    }

    @Test
    public void delete() {
        IObjectData objectData = new ObjectData();
        IObjectData objectData1 = (IObjectData) executeDelete(objectData);
    }

    @Test
    public void insertImportActionTest() {
        BaseImportTemplateAction.Arg arg = new BaseImportTemplateAction.Arg();
        arg.setDescribeApiName(PrepayDetailConstants.API_NAME);
        arg.setImportType(SystemConstants.ImportType.Insert.value);
        execute(StandardAction.InsertImportTemplate.name(), arg);
    }
}

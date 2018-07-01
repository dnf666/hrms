package com.facishare.crm.stock.action;

import com.facishare.crm.stock.base.BaseActionTest;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportVerifyAction;
import com.facishare.paas.metadata.impl.ObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

/**
 * Created by linchf on 2018/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class WareHouseActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public WareHouseActionTest() {
        super(WarehouseConstants.API_NAME);
    }

    @Test
    public void testBulkInvalid() {
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson("\n" +
                "    {\"dataList\":[{\"object_describe_id\":\"5a6178d8fa12271d7ca892f0\",\"object_describe_api_name\":\"WarehouseObj\",\"apiname\":\"WarehouseObj\",\"_id\":\"5a61976e830bdb637ca52b79\",\"dataId\":\"5a61976e830bdb637ca52b79\",\"tenant_id\":\"55424\"}]}");
        Object result = executeBulkInvalid(arg.getJson());
        System.out.println(result);
    }

    @Test
    public void testInsertImportVerify() {
        StandardInsertImportVerifyAction.Arg arg = new BaseImportAction.Arg();
        arg.setApiName(WarehouseConstants.API_NAME);
        arg.setTenantId("2");
        arg.setUserId("1000");
        ObjectData objectData = new ObjectData();
        objectData.set("1", "2");
        arg.setRows(Arrays.asList(ObjectDataDocument.of(objectData)));
        arg.setImportType(1);
        execute(StandardAction.InsertImportVerify.name(), arg);
    }
}

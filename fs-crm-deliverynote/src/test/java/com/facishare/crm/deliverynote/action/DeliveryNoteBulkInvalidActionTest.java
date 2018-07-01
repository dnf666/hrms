package com.facishare.crm.deliverynote.action;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.base.BaseActionTest;
import com.facishare.paas.appframework.core.model.JSONSerializer;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.SerializerManagerImpl;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class DeliveryNoteBulkInvalidActionTest extends BaseActionTest {

    public DeliveryNoteBulkInvalidActionTest() {
        super(DeliveryNoteObjConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public void initUser() {
        this.tenantId = "55985";
        this.fsUserId = "1000";
    }

    @Test
    public void bulkInvalid_Success() {
        SerializerManagerImpl serializerManager = new SerializerManagerImpl();
        JSONSerializer serializer = serializerManager.getSerializer(RequestContext.ContentType.FULL_JSON);
        String argJson = "{\"json\":\"{\\\"dataList\\\":[{\\\"object_describe_id\\\":\\\"5a659a04830bdbac27873f38\\\",\\\"object_describe_api_name\\\":\\\"DeliveryNoteObj\\\",\\\"apiname\\\":\\\"DeliveryNoteObj\\\",\\\"_id\\\":\\\"5add6ed2bab09cee4911d2c5\\\",\\\"dataId\\\":\\\"5add6ed2bab09cee4911d2c5\\\",\\\"tenant_id\\\":\\\"55985\\\"}]}\"}";
        StandardBulkInvalidAction.Arg arg = serializer.decode(StandardBulkInvalidAction.Arg.class, argJson);

        execute(StandardAction.BulkInvalid.name(), arg);
    }

}

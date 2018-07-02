package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.predefine.base.BaseActionTest;
import com.facishare.paas.appframework.core.model.JSONSerializer;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.SerializerManagerImpl;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
//
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class InternalSignCertifyEditActionTest extends BaseActionTest {

    static {
        System.setProperty("process.profile", "ceshi113");
    }

    public InternalSignCertifyEditActionTest() {
        super(InternalSignCertifyObjConstants.API_NAME);
    }

    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = "1000";
    }

    @Test
    public void edit_Success() {
        SerializerManagerImpl serializerManager = new SerializerManagerImpl();
        JSONSerializer serializer = serializerManager.getSerializer(RequestContext.ContentType.FULL_JSON);
        String argJson = "{\n" +
                "        \"record_type\":\"default__c\",\n" +
                "        \"object_describe_api_name\":\"InternalSignCertifyObj\",\n" +
                "        \"object_describe_id\":\"5afc021e5ce261aabff7f8bb\",\n" +
                "        \"reg_mobile\":\"18951765075\",\n" +
                "        \"enterprise_name\":\"小米科技有限责任公司\",\n" +
                "        \"unified_social_credit_identifier\":\"91110108551385082Q\",\n" +
                "        \"legal_person_name\":\"王海峰\",\n" +
                "        \"legal_person_mobile\":\"18951765075\",\n" +
                "        \"legal_person_identity\":\"610427198406172518\",\n" +
                "        \"use_status\":\"1\",\n" +
                "        \"version\":\"3\",\n" +
                "        \"_id\":\"5afc0fdebab09c65776cbf9d\"\n" +
                "    }";
        IObjectData arg = serializer.decode(ObjectDataDocument.class, argJson).toObjectData();

        executeEdit(arg);
    }
}

package com.facishare.crm.deliverynote.action;

import com.facishare.crm.deliverynote.base.BaseActionTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.paas.appframework.core.model.JSONSerializer;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.SerializerManagerImpl;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.metadata.api.IObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class DeliveryNoteEditActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public void initUser() {
        this.tenantId = "55983";
        this.fsUserId = "1000";
    }

    public DeliveryNoteEditActionTest() {
        super(DeliveryNoteObjConstants.API_NAME);
    }

    @Test
    public void edit_Success() {
        SerializerManagerImpl serializerManager = new SerializerManagerImpl();
        JSONSerializer serializer = serializerManager.getSerializer(RequestContext.ContentType.FULL_JSON);
        String argJson = "{\"object_data\":{\"record_type\":\"default__c\",\"object_describe_api_name\":\"DeliveryNoteObj\",\"object_describe_id\":\"5a6e9d2b830bdbe4af4d033c\",\"sales_order_id\":\"34e1bc3dac52468d890cac404247d853\",\"delivery_date\":1525276800000,\"express_org\":\"\",\"express_order_id\":\"\",\"total_delivery_money\":\"50.00\",\"delivery_warehouse_id\":\"5ac3381c830bdb4f3a7f4c88\",\"remark\":\"\",\"version\":\"28\",\"_id\":\"5aec4de9bab09c0682752b13\"},\"details\":{\"DeliveryNoteProductObj\":[{\"version\":\"4\",\"_id\":\"5aec4decbab09c0682752d44\",\"sales_order_id\":\"34e1bc3dac52468d890cac404247d853\",\"product_id\":\"f87e2400793c4a18b90816007abea82a\",\"specs\":\"\",\"unit\":\"千克\",\"order_product_amount\":1,\"has_delivered_num\":0,\"delivery_num\":\"0.50\",\"avg_price\":\"100.00\",\"delivery_money\":\"50.00\",\"real_stock\":\"200.00\",\"field_X6z0v__c\":\"0.00\",\"object_describe_id\":\"5a6e9d2c830bdbe4af4d035d\",\"object_describe_api_name\":\"DeliveryNoteProductObj\",\"record_type\":\"default__c\",\"stock_id\":\"5ac33873830bdb4f3a7f4c92\"}]}}";
        IObjectData arg = serializer.decode(ObjectDataDocument.class, argJson).toObjectData();

        executeEdit(arg);
    }

}

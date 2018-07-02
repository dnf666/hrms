package com.facishare.crm.deliverynote.action;

import com.facishare.crm.deliverynote.base.BaseActionTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.predefine.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.model.JSONSerializer;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.SerializerManagerImpl;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class DeliveryNoteAddActionTest extends BaseActionTest {
    static {
        System.setProperty("process.profile", "fstest");
        System.setProperty("spring.profiles.active", "fstest");
    }

    public void initUser() {
        this.tenantId = "71592";  // fsceshi021
        this.fsUserId = "1000";
    }

    public DeliveryNoteAddActionTest() {
        super(DeliveryNoteObjConstants.API_NAME);
    }

    @Test
    public void add_Success() {
        SerializerManagerImpl serializerManager = new SerializerManagerImpl();
        JSONSerializer serializer = serializerManager.getSerializer(RequestContext.ContentType.FULL_JSON);
        String argJson = "{\"object_data\":{\"record_type\":\"default__c\",\"object_describe_api_name\":\"DeliveryNoteObj\",\"object_describe_id\":\"5ae9a16e7cfed9d10949e18b\",\"sales_order_id\":\"d2977456f75b4e108c592b1c5068de3e\",\"delivery_date\":1525104000000,\"express_org\":\"\",\"express_order_id\":\"\",\"total_delivery_money\":\"480.00\",\"owner\":[\"1000\"],\"remark\":\"\"},\"details\":{\"DeliveryNoteProductObj\":[{\"sales_order_id\":\"d2977456f75b4e108c592b1c5068de3e\",\"product_id\":\"a12ea2a494e84c1d93f768e7033a02b9\",\"specs\":\"\",\"unit\":\"个\",\"order_product_amount\":1,\"has_delivered_num\":0.2,\"delivery_num\":0.8,\"avg_price\":100,\"delivery_money\":\"80.00\",\"object_describe_id\":\"5ae9a16f7cfed9d10949e1ad\",\"object_describe_api_name\":\"DeliveryNoteProductObj\",\"record_type\":\"default__c\"},{\"sales_order_id\":\"d2977456f75b4e108c592b1c5068de3e\",\"product_id\":\"70caa72934f544a0b6d095ec3f004bf1\",\"specs\":\"\",\"unit\":\"把\",\"order_product_amount\":1,\"has_delivered_num\":0.2,\"delivery_num\":0.8,\"avg_price\":200,\"delivery_money\":\"160.00\",\"object_describe_id\":\"5ae9a16f7cfed9d10949e1ad\",\"object_describe_api_name\":\"DeliveryNoteProductObj\",\"record_type\":\"default__c\"},{\"sales_order_id\":\"d2977456f75b4e108c592b1c5068de3e\",\"product_id\":\"60b9912e179749fcab83c227ef10746a\",\"specs\":\"\",\"unit\":\"套\",\"order_product_amount\":1,\"has_delivered_num\":0.2,\"delivery_num\":0.8,\"avg_price\":300,\"delivery_money\":\"240.00\",\"object_describe_id\":\"5ae9a16f7cfed9d10949e1ad\",\"object_describe_api_name\":\"DeliveryNoteProductObj\",\"record_type\":\"default__c\"}]}}";
        BaseObjectSaveAction.Arg arg = serializer.decode(BaseObjectSaveAction.Arg.class, argJson);

        executeAdd(arg.getObjectData().toObjectData(), arg.getDetails());
    }

}

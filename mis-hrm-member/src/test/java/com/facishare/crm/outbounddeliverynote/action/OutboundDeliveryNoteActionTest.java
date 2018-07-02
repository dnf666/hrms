package com.facishare.crm.outbounddeliverynote.action;

import com.facishare.crm.outbounddeliverynote.base.BaseActionTest;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author linchf
 * @date 2018/3/21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class OutboundDeliveryNoteActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public OutboundDeliveryNoteActionTest() { super(OutboundDeliveryNoteConstants.API_NAME); }


    @Test
    public void testBulkInvalid() {
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson("{\"dataList\":[{\"object_describe_id\":\"5ac4828cbab09c646763dd90\",\"object_describe_api_name\":\"OutboundDeliveryNoteObj\",\"_id\":\"5ad48f20bab09cb8bd7050ea\",\"tenant_id\":\"55985\"}]}");
        Object result = executeBulkInvalid(arg.getJson());
        System.out.println(result);
    }

    @Test
    public void testEdit() {
        StandardEditAction.Arg arg = new StandardEditAction.Arg();
        ObjectData objectData = new ObjectData();
        objectData.setDescribeApiName("OutboundDeliveryNoteObj");
        objectData.setDescribeId("5aea716ea5083ddc685f9387");
        objectData.set("outbound_date", "1526227200000");
        objectData.set("outbound_type", "1");
        objectData.set("record_type", "default__c");
        objectData.set("warehouse_id", "5ae9a7047cfed9d10949e25d");
        objectData.setVersion(10);
        objectData.setId("5af94f8fa5083d58688d8327");
        arg.setObjectData(ObjectDataDocument.of(objectData));

        Map<String, List<ObjectDataDocument>> details = Maps.newHashMap();
        List<ObjectDataDocument> objectDataDocuments = Lists.newArrayList();
        ObjectData product1 = new ObjectData();
        product1.setDescribeApiName("OutboundDeliveryNoteProductObj");
        product1.setDescribeId("5aea716ea5083ddc685f93bc");
        product1.setVersion(7);
        product1.set("record_type", "default__c");
        product1.setId("5af94f8fa5083d58688d833c");
        product1.set("available_stock", "170.00");
        product1.set("outbound_amount", "1.00");
        product1.set("product_id", "d547e6ae54b6419daa785d88ed68b945");
        product1.set("stock_id", "5af57310a5083d6f0002169e");
        objectDataDocuments.add(ObjectDataDocument.of(product1));

        ObjectData product2 = new ObjectData();
        product2.setDescribeApiName("OutboundDeliveryNoteProductObj");
        product2.setDescribeId("5aea716ea5083ddc685f93bc");
        product2.set("record_type", "default__c");
        product2.set("available_stock", "25.00");
        product2.set("outbound_amount", "1.00");
        product2.set("product_id", "132901a141aa45c7b4d89de6a91297ab");
        product2.set("stock_id", "5af2e02aa5083df0162d84fd");
        objectDataDocuments.add(ObjectDataDocument.of(product2));
        details.put("OutboundDeliveryNoteProductObj", objectDataDocuments);
        arg.setDetails(details);
        execute(StandardAction.Edit.name(), arg);
    }
}

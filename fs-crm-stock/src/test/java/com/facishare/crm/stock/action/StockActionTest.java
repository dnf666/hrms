package com.facishare.crm.stock.action;

import com.facishare.crm.stock.base.BaseActionTest;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public StockActionTest() {
        super(StockConstants.API_NAME);
    }

    @Test
    public void addWarehouseTest() {
        //        String customerId = "15b5734b742244f3a55e0f3245105fde";
        IObjectData objectData = new ObjectData();
        objectData.setRecordType("default__c");
        objectData.set("object_describe_api_name", "GoodsReceivedNoteObj");
        objectData.set("object_describe_id", "5a583c74fa122729945f55d7");
        objectData.set("goods_received_date", "1514822400000");
        objectData.set("warehouse_id", "5a5849e6830bdbe53c1eefe2");
        objectData.set("goods_received_type", "1");
        objectData.set("remark", "");
        objectData.set("owner", new String[] {"1069"});



        List<IObjectData> details = Lists.newArrayList();

        ObjectDataDocument objectDataDocument = new ObjectDataDocument();
        objectDataDocument.put("price_book_product_id", null);
        objectDataDocument.put("product_id", "d0a69d2da4f94d5eb49c5a88de6cbac8");
        objectDataDocument.put("is_give_away", "");
        objectDataDocument.put("specs", "");
        objectDataDocument.put("unit", "只");
        objectDataDocument.put("goods_received_amount", "200");
        objectDataDocument.put("remark", "");
        objectDataDocument.put("object_describe_id", "5a583c7afa122729945f55f6");
        objectDataDocument.put("object_describe_api_name", "GoodsReceivedNoteProductObj");
        objectDataDocument.put("record_type", "default__c");
        Map<String, List<ObjectDataDocument>> detailsMap = Maps.newHashMap();
        detailsMap.put("GoodsReceivedNoteProductObj", Arrays.asList(objectDataDocument));


        Object result = executeAdd(objectData, detailsMap);
        System.out.println("addCustomerAccount->result=" + result);
    }
}

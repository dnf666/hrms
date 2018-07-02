package com.facishare.crm.stock.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facishare.crm.stock.base.BaseActionTest;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 21/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class GoodsReceivedNoteActionTest extends BaseActionTest {

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public GoodsReceivedNoteActionTest() { super(GoodsReceivedNoteConstants.API_NAME); }

    @Test
    public void testGoodsReceivedNoteBulkInvalid() {
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson("{\"dataList\":[{\"object_describe_id\":\"5a702fb1fa122729d00ba5bf\",\"object_describe_api_name\":\"GoodsReceivedNoteObj\",\"apiname\":\"GoodsReceivedNoteObj\",\"_id\":\"5a77f8697cfed9daf9492abf\",\"dataId\":\"5a77f8697cfed9daf9492abf\",\"tenant_id\":\"69636\"}]}");
        Object result = executeBulkInvalid(arg.getJson());
        System.out.println(result);

    }

    @Test
    public void testEditAction() {
        String objectDataString = "{\"object_data\":{\"record_type\":\"default__c\",\"object_describe_api_name\":\"GoodsReceivedNoteObj\",\"object_describe_id\":\"5a7432daa5083daf3864b49a\",\"goods_received_date\":1518105600000,\"warehouse_id\":\"5a743c02a5083dbe6ed1c31b\",\"goods_received_type\":\"1\",\"remark\":\"\",\"version\":\"5\",\"_id\":\"5a7d538d7cfed9e9d9cb8cb0\"},\"details\":{\"GoodsReceivedNoteProductObj\":[{\"version\":\"2\",\"_id\":\"5a7d538e7cfed9e9d9cb8cb6\",\"product_id\":\"015a074368ac43328a8ab9456f5a9fde\",\"is_give_away\":\"\",\"specs\":\"\",\"unit\":\"把\",\"goods_received_amount\":\"20.00\",\"object_describe_id\":\"5a7432daa5083daf3864b4ba\",\"object_describe_api_name\":\"GoodsReceivedNoteProductObj\",\"record_type\":\"default__c\"},{\"version\":\"2\",\"_id\":\"5a7d538e7cfed9e9d9cb8cb5\",\"product_id\":\"91dd2d8215cb462bb7e0a380388f3a9c\",\"is_give_away\":\"是\",\"specs\":\"\",\"unit\":\"套\",\"goods_received_amount\":\"10.00\",\"object_describe_id\":\"5a7432daa5083daf3864b4ba\",\"object_describe_api_name\":\"GoodsReceivedNoteProductObj\",\"record_type\":\"default__c\"}]}}";
        Map<String, Map<String, Object>> objectMap = JSON.parseObject(objectDataString, new TypeReference<HashMap<String, Map<String, Object>>>() {});
        Map<String, Object> objectDataMap = objectMap.get("object_data");
        IObjectData goodsReceivedNoteObj = ObjectDataExt.of(objectDataMap).getObjectData();
        Object result = executeEdit(goodsReceivedNoteObj);
        System.out.println(result);
    }

    @Test
    public void testWorkFlowCompletedAction() {

        StandardFlowCompletedAction.Arg arg = new StandardFlowCompletedAction.Arg();
        arg.setDataId("5a6990f8830bdb8235887360");
        arg.setDescribeApiName(GoodsReceivedNoteConstants.API_NAME);
        arg.setStatus("pass");
        arg.setTriggerType(3);
        arg.setTenantId("55985");
        arg.setUserId(User.SUPPER_ADMIN_USER_ID);

        execute(StandardAction.FlowCompleted.name(), arg);
    }

    @Test
    public void testAddWithWorkFlow() {
        IObjectData objectData = new ObjectData();
        objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        objectData.set("object_describe_api_name", "GoodsReceivedNoteObj");
        objectData.set("object_describe_id", "5ae95b437cfed989852aef25");
        objectData.set("goods_received_date", "1525968000000");
        objectData.set("warehouse_id", "5aea8425a5083d45d86d4c17");
        objectData.set("goods_received_type", "1");
        objectData.set("remark", "");
        objectData.set("owner", new String[] {"1000"});

        Map<String, List<ObjectDataDocument>> details = Maps.newHashMap();
        Map<String, Object> map = Maps.newHashMap();
        map.put("goods_received_amount", 1);
        map.put("is_give_away", "");
        map.put("object_describe_api_name", "GoodsReceivedNoteProductObj");
        map.put("object_describe_id", "5ae95b437cfed989852aef45");
        map.put("product_id", "6caf1704aae74f4caf3825af5acfce4d");
        map.put("record_type", "default__c");
        map.put("remark", "");
        map.put("specs", "");
        map.put("unit", "把");

        ObjectDataDocument goodsReceivedNoteProductObj = new ObjectDataDocument();
        goodsReceivedNoteProductObj = ObjectDataDocument.of(map);
        List<ObjectDataDocument> objectDataDocuments = Lists.newArrayList();
        objectDataDocuments.add(goodsReceivedNoteProductObj);

        details.put("GoodsReceivedNoteProductObj", objectDataDocuments);

        Object result = executeAdd(objectData, details);
        System.out.println(result);
    }
}

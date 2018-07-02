package com.facishare.crm.requisitionnote.Action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facishare.crm.requisitionnote.base.BaseActionTest;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
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
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 26/03/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RequisitionNoteActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public RequisitionNoteActionTest() {
        super(RequisitionNoteConstants.API_NAME);
    }

    @Test
    public void requisitionNoteAddActionTest() {
        IObjectData objectData = new ObjectData();
        objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        objectData.set("object_describe_api_name", RequisitionNoteConstants.API_NAME);
        objectData.set("object_describe_id", "5ab31e8c760edd5d7be1b1da");
        objectData.set(RequisitionNoteConstants.Field.RequisitionDate.apiName, 1522033052000L);
        objectData.set(RequisitionNoteConstants.Field.TransferInWarehouse.apiName, "5a9e3a98830bdb56facb0886");
        objectData.set(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, "5a659bcf830bdbac278740a6");
        objectData.set("remark", "");
        objectData.set("owner", new String[] {"1069"});


        ObjectDataDocument objectDataDocument = new ObjectDataDocument();
        objectDataDocument.put(RequisitionNoteProductConstants.Field.AvailableStock.apiName, "2681");
        objectDataDocument.put("product_id", "1e265f44575a4aedaf9d2d2f92218471");
        objectDataDocument.put(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName, "10");
        objectDataDocument.put("specs", "");
        objectDataDocument.put("unit", "个");
        objectDataDocument.put(RequisitionNoteProductConstants.Field.Stock.apiName, "5a659c48830bdbac278740e9");
        objectDataDocument.put("remark", "");
        objectDataDocument.put("object_describe_id", "5ab31e91760edd5d7be1b1fd");
        objectDataDocument.put("object_describe_api_name", RequisitionNoteProductConstants.API_NAME);
        objectDataDocument.put("record_type", "default__c");
        Map<String, List<ObjectDataDocument>> detailsMap = Maps.newHashMap();
        detailsMap.put("RequisitionNoteProductObj", Arrays.asList(objectDataDocument));


        Object result = executeAdd(objectData, detailsMap);
        System.out.println("addRequisitionNote->result=" + result);
    }

    @Test
    public void requisitionNoteBulkInvalidActionTest() {
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson("{\"dataList\":[{\"object_describe_id\":\"5ac4828abab09c646763dd0b\",\"object_describe_api_name\":\"RequisitionNoteObj\",\"apiname\":\"RequisitionNoteObj\",\"_id\":\"5acf3fedbab09c11e3b30077\",\"dataId\":\"5acf3fedbab09c11e3b30077\",\"tenant_id\":\"55985\"}]}");
        Object result = executeBulkInvalid(arg.getJson());
        System.out.println(result);
    }

    @Test
    public void requisitionNoteEditActionTest() {
/*        String objectDataString = "{\"object_data\":{\"record_type\":\"default__c\",\"object_describe_api_name\":\"RequisitionNoteObj\",\"object_describe_id\":\"5aea716ca5083ddc685f92b2\",\"requisition_date\":1525363200000,\"transfer_out_warehouse_id\":\"5aea8425a5083d45d86d4c17\",\"transfer_in_warehouse_id\":\"5ae9a7047cfed9d10949e25d\",\"remark\":\"\",\"version\":\"10\",\"_id\":\"5aec10a1a5083dfc6e66dc93\"},\"details\":{\"RequisitionNoteProductObj\":[{\"version\":\"6\",\"_id\":\"5aec10a4a5083dfc6e66deb6\",\"product_id\":\"b76aa4b2199e4b44aebe5b523efe7368\",\"specs\":\"\",\"unit\":\"块\",\"available_stock\":\"105.00\",\"requisition_product_amount\":\"5.00\",\"remark\":\"gg\",\"object_describe_id\":\"5aea716ca5083ddc685f92f9\",\"object_describe_api_name\":\"RequisitionNoteProductObj\",\"record_type\":\"default__c\",\"stock_id\":\"5aeab1b8a5083d45d86eed5e\"}]}}";
        Map<String, Map<String, Object>> objectMap = JSON.parseObject(objectDataString, new TypeReference<HashMap<String, Map<String, Object>>>() {});
        Map<String, Object> objectDataMap = objectMap.get("object_data");
        String detailString = JSON.toJSONString(objectMap.get("details"));

        Map<String, List<ObjectDataDocument>> details = JSON.parseObject(detailString, new TypeReference<HashMap<String, List<ObjectDataDocument>>>() {});
        IObjectData objectData = ObjectDataExt.of(objectDataMap).getObjectData();
        Object result = executeEdit(objectData, details);
        System.out.println(result);*/
    }

    @Test
    public void requisitionNoteFlowCompletedActionTest() {
        StandardFlowCompletedAction.Arg arg = new StandardFlowCompletedAction.Arg();
        arg.setDataId("5a6990f8830bdb8235887360");
        arg.setDescribeApiName(GoodsReceivedNoteConstants.API_NAME);
        arg.setStatus("pass");
        arg.setTriggerType(3);
        arg.setTenantId("55985");
        arg.setUserId(User.SUPPER_ADMIN_USER_ID);

        execute(StandardAction.FlowCompleted.name(), arg);
    }
}

package com.facishare.crm.stock.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liangk
 * @date 21/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class GoodsReceivedNoteManagerTest extends BaseTest {
    @Resource
    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    public GoodsReceivedNoteManagerTest() {}

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testBuildGoodsReceivedNoteProduct() {
        User user = new User("2", "1000");
        String jsonString = "{\"tenant_id\":\"2\",\"goods_received_date\":1516118400000,\"is_deleted\":false,\"total_num\":1,\"object_describe_api_name\":\"GoodsReceivedNoteObj\",\"owner_department\":\"112\",\"goods_received_type\":\"1\",\"owner\":[\"1069\"],\"lock_status\":\"0\",\"package\":\"CRM\",\"last_modified_time\":1516546005606,\"create_time\":1516176442997,\"life_status\":\"invalid\",\"last_modified_by\":\"1069\",\"created_by\":\"1069\",\"record_type\":\"default__c\",\"relevant_team\":[{\"teamMemberEmployee\":[\"1069\"],\"teamMemberRole\":\"1\",\"teamMemberPermissionType\":\"2\"}],\"object_describe_id\":\"5a5863b0fa12271cf0216dfd\",\"name\":\"GRN2018-01-17_11\",\"_id\":\"5a5f043a830bdbbe22cdb488\",\"warehouse_id\":\"5a5c665d830bdb232000b305\"}";

        Map<String, Object> objectMap = JSON.parseObject(jsonString, new TypeReference<HashMap<String,Object>>() {});

        IObjectData goodsReceivedNoteObj = ObjectDataExt.of(objectMap).getObjectData();

        String id = goodsReceivedNoteObj.get("_id").toString();

        GoodsReceivedNoteProductModel.BuildProductResult result = goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(user, goodsReceivedNoteObj);
        System.out.println("-----------------------------------------");
        System.out.println(result);
    }

    @Test
    public void testAddfieldDescribeAndLayout() {
        User user = new User("55983", "1000");
        goodsReceivedNoteManager.addFieldDescribeAndLayout(user);
    }
}

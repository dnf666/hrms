package com.facishare.crm.stock.controller;

import com.facishare.crm.stock.base.BaseControllerTest;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by linchf on 2018/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class WareHouseControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    public WareHouseControllerTest() {
        super(WarehouseConstants.API_NAME);
    }

    @Test
    public void testDetail() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        arg.setObjectDataId("5a780c8f830bdb2a7298a956");
        arg.setObjectDescribeApiName(WarehouseConstants.API_NAME);
        Object result = executeDetail(arg);
        System.out.println(result);
    }

    @Test
    public void testListHeader() {
        StandardListHeaderController.Arg arg = new StandardListHeaderController.Arg();
        arg.setApiName(WarehouseConstants.API_NAME);
        arg.setIncludeLayout(true);
        arg.setLayoutType("list");
        arg.setRecordTypeAPIName("default__c");
        Object result = execute("ListHeader", arg);
        System.out.println(result);
    }

    @Test
    public void testRelatedList() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();

        arg.setObjectApiName(WarehouseConstants.API_NAME);
        arg.setRelatedListName("shipping_warehouse_sales_order_list");
        arg.setSearchTemplateId("5a659b54830bdbac2787409f");
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");
        ObjectDataDocument objectData = new ObjectDataDocument();
        objectData.put("account_id", "e832eccbfe4b4a069d5197165fd6bd49");
//        objectData.put("filter_enable", "true");

        arg.setObjectData(objectData);

        Object result = execute(StandardController.RelatedList.name(), arg);
        String s = result.toString();
        System.out.println(result);
    }

    @Test
    public void testList() {
        StandardListController.Arg arg = new StandardListController.Arg();
        arg.setObjectDescribeApiName(WarehouseConstants.API_NAME);
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");
        arg.setSearchTemplateId("5a659b54830bdbac2787409f");
        Object result = execute("List", arg);
        System.out.println(result);
    }

}

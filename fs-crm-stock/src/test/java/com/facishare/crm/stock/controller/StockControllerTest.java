package com.facishare.crm.stock.controller;

import com.facishare.crm.stock.base.BaseControllerTest;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.core.predef.controller.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by linchf on 2018/1/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public StockControllerTest() {
        super(StockConstants.API_NAME);
    }

    @Test
    public void testList() {
        StandardListController.Arg arg = new StandardListController.Arg();
        arg.setObjectDescribeApiName(StockConstants.API_NAME);
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");
//        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[{\"field_name\":\"product_id.name\",\"field_values\":[\"232\"],\"operator\":\"LIKE\"}]}");
        arg.setSearchTemplateId("5ae96ecd7cfed9acc83e5225");
        Object result = execute("List", arg);
        System.out.println(result);
    }

    @Test
    public void testListHeader() {
        StandardListHeaderController.Arg arg = new StandardListHeaderController.Arg();
        arg.setApiName(StockConstants.API_NAME);
        arg.setIncludeLayout(true);
        arg.setLayoutType("list");
        Object result = execute("ListHeader", arg);
        System.out.println(result);
    }

    @Test
    public void testDetailController() {
        StandardDetailController.Arg arg = new StandardDetailController.Arg();
        //arg.setObjectDataId("5ab32694760edd5f1e4f3eca");
        arg.setObjectDataId("5a79593f830bdbfc732ac713");
        arg.setObjectDescribeApiName(StockConstants.API_NAME);

        Object result = executeDetail(arg);
        System.out.println(result);
    }

    @Test
    public void testRelatedList() {
        StandardRelatedListController.Arg arg = new StandardRelatedListController.Arg();

        arg.setTargetObjectDataId("5a659bcf830bdbac278740a6");
        arg.setTargetObjectApiName(WarehouseConstants.API_NAME);
        arg.setObjectApiName(StockConstants.API_NAME);
        arg.setRelatedListName("target_related_list_stock_warehouse");
        arg.setIncludeAssociated(true);
        arg.setSearchQueryInfo("{\"limit\":20,\"offset\":0,\"filters\":[]}");


        Object result = execute(StandardController.RelatedList.name(), arg);
        String s = result.toString();
        System.out.println(result);
    }


}

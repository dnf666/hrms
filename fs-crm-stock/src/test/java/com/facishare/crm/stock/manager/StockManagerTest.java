package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.base.BaseTest;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liangk
 * @date 13/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class StockManagerTest extends BaseTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private StockManager stockManager;

    @Resource
    private WareHouseManager wareHouseManager;

    @Test
    public void testQueryStocksByProductIds() {
        List<String> productIdList = Lists.newArrayList();
        productIdList.add("20d63c07b11a43a1ae6f3e4dd8b6a0b4");
        productIdList.add("0bb6054db53a47bab004155c034142ed");
        productIdList.add("d0a69d2da4f94d5eb49c5a88de6cbac8");
        String wareHouse = "5a587152830bdbe53c1ef1fc";
        List<IObjectData> result = stockManager.queryByWarehouseIdAndProductIds(new User("2", "1000"),wareHouse, productIdList);

        System.out.println(result);
    }

    @Test
    public void testQueryStocksByWarehouseIds() {
        List<String> productIdList = Lists.newArrayList();
        productIdList.add("f091909e263e4806bfad0cd727dceca9");
        productIdList.add("861bec14f7414c6b8804fa33d002e4d6");
        productIdList.add("65347dde742e48e8bd5feae3a3de06e1");

        List<String> warehouseIdList = Lists.newArrayList();
        warehouseIdList.add("5a94cf50830bdbaac2fa1773");
        warehouseIdList.add("5a8e6133830bdb989b6f581e");
        List<IObjectData> result = stockManager.queryStocksByWarehouseIdsAndProductIds(new User("55985", "1000"), productIdList, warehouseIdList);

        System.out.println(result);
    }

    @Test
    public void testCheckStockAndSendWarningMsg() {
        stockManager.checkStockAndSetRemindRecord("55985");
    }

    @Test
    public void testAddFieldDescribeAndLayout() {
        User user = new User("55983", "1000");
        stockManager.addFieldDescribeAndLayout(user);
    }

    @Test
    public void testQueryGoodsSendingPersons() {
        List<String> users = stockManager.queryGoodsSendingPersons("55985");
        System.out.print(users);
    }

    @Test
    public void testIsShowStockWarningMenu() {
        stockManager.isShowStockWarningMenu(new User("55983", "1000"));
    }

}

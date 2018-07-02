package com.facishare.crm.stock.manager;

import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.ReturnOrderModel;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.stock.predefine.manager.ProductManager;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 13/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class ProductManagerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private ProductManager productManager;

    @Resource
    private ServiceFacade serviceFacade;

    @Test
    public void testProduct() {
        List<String> ids = Lists.newArrayList();
        ids.add("eaf887190dea445caf7c0620e173111d");
        ids.add("538a750fe90b436084d8d5cf43cecae7");
        List<IObjectData> result = serviceFacade.findObjectDataByIdsIncludeDeleted(new User("55985", "1000"), ids, "ProductObj");
        System.out.println(result);
    }

    @Test
    public void testQueryProductByIds() {
        List<String> productIdList = Lists.newArrayList();
        productIdList.add("20d63c07b11a43a1ae6f3e4dd8b6a0b4");
        List<QueryProductByIds.ProductVO> result = productManager.queryProductByIds(new User("2", "1000"), productIdList);

        System.out.println(result);
    }

    @Test
    public void testGetProductsByOrderId() {
        productManager.getProductsByOrderId(new User("55983", "1000"), "a6006903c0ce4e97a81ffc90a6ed238b", true);
    }

    @Test
    public void testQueryProductsByOrderIds() {
        Map<String, List<SalesOrderModel.SalesOrderProductVO>> result = productManager.queryProductsByOrderIds(new User("55985", "1000"), Arrays.asList("62a05bb7e8244c1483aee45ef1ab5494","e22d3a2706b6458e8900e2a266d3c530"));
        System.out.println(result);
    }

    @Test
    public void testQueryReturnOrderProductsByReturnOrderIds() {
        Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> result = productManager.queryReturnOrderProductsByReturnOrderIds(new User("55985", "1000"), Arrays.asList("a1dcf93a8c8d4c8fb16b6edea337864f","3f47bf9860f243f0820629d4eb2ab5f8"));
        System.out.println(result);
    }


    @Test
    public void testQueryReturnProductByIds() {
        List<ReturnOrderModel.ReturnOrderProductVO> result =
                productManager.getReturnProductsByOrderId(new User("2", "1000"), "b7ed6db03125410fa48eee5d79214dd7");

        System.out.println(result);
    }
}

package com.facishare.crm.stock.service;

import com.facishare.crm.stock.base.BaseServiceTest;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.predefine.service.ProductService;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class ProductServiceTest extends BaseServiceTest {
    @Resource
    private ProductService productService;

    public ProductServiceTest() {
        super(StockConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void queryProductByIdsTest() {
        StockType.QueryProductByIdsArg arg = new StockType.QueryProductByIdsArg();

        List<String> productIds = new ArrayList<>();
        productIds.add("538a750fe90b436084d8d5cf43cecae7");
        arg.setProductIds(productIds);
        StockType.QueryProductByIdsResult result = productService.queryProductByIds(newServiceContext(), arg);
        System.out.println(result);
    }
}

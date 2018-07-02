package com.facishare.crm.stock.service;

import com.facishare.crm.stock.base.BaseServiceTest;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.predefine.service.StockService;
import com.facishare.crm.stock.predefine.service.dto.*;
import com.facishare.crm.stock.predefine.service.model.QueryAvailableStocksModel;
import com.facishare.crm.stock.predefine.service.model.QueryByProductsIdModel;
import com.facishare.crm.stock.predefine.service.model.QueryStockConfigModel;
import com.facishare.crm.stock.predefine.service.model.SaveStockConfigModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockServiceTest extends BaseServiceTest {
    @Autowired
    private StockService stockService;

    public StockServiceTest() {
        super(StockConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void enableStockTest() throws MetadataServiceException {
        StockType.EnableStockResult enableResult = stockService.enableStock(newServiceContext());
        System.out.println(enableResult);
    }

    @Test
    public void queryStockConfigTest() {
        QueryStockConfigModel.Result configResult = stockService.queryStockConfig(newServiceContext());
        System.out.println(configResult);
    }

    @Test
    public void saveStockConfigTest() {
        SaveStockConfigModel.Arg arg = new SaveStockConfigModel.Arg();
        arg.setStockViewType(StockViewType.StockViewTypeEnum.ACCURATE.getStringStatus());
        arg.setValidateOrderType(OrderCheckType.OrderCheckTypeEnum.CANNOTSUBMIT.getStringStatus());
        arg.setStockWarningType(StockWarningType.StockWarningTypeEnum.ENABLE.getStringStatus());
        arg.setOrderWarehouseType(OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE.getStringStatus());
        SaveStockConfigModel.Result result = stockService.saveStockConfig(newServiceContext(), arg);

        System.out.println(result);
    }

    @Test
    public void queryByProductsIdTest() {
        QueryByProductsIdModel.Result result = new QueryByProductsIdModel.Result();

        ServiceContext serviceContext = newServiceContext();
        String accountId = "ad71b92b6f3b4efa956d4e6ca0c3b624";
        List<String> productIds = Lists.newArrayList();
        productIds.add("df2274532dea406ab0ddc7ca9ddfc4ce");
        //productIds.add("20d63c07b11a43a1ae6f3e4dd8b6a0b4");
        QueryByProductsIdModel.Arg arg = new QueryByProductsIdModel.Arg();
        arg.setAccountId(accountId);
        arg.setProductIds(productIds);

        result = stockService.queryByProductsId(serviceContext, arg);

        System.out.println(result);

    }

    @Test
    public void querySalesOrderProductAvailableStockTest() {
        QueryAvailableStocksModel.Result result = new QueryAvailableStocksModel.Result();
        ServiceContext serviceContext = newServiceContext();

        QueryAvailableStocksModel.Arg arg = new QueryAvailableStocksModel.Arg();
        arg.setCustomerId("e832eccbfe4b4a069d5197165fd6bd49");
        arg.setWarehouseId("5a9e3a98830bdb56facb0886");
        List<String> productIds = Lists.newArrayList();
        productIds.add("1e265f44575a4aedaf9d2d2f92218471");
        productIds.add("f091909e263e4806bfad0cd727dceca9");
        arg.setProductIds(productIds);
        result = stockService.querySalesOrderProductAvailableStock(serviceContext, arg);
        System.out.println(result);
    }
}

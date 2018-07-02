package com.facishare.crm.stock.service;

import com.facishare.crm.stock.base.BaseServiceTest;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.service.WareHouseService;
import com.facishare.crm.stock.predefine.service.model.QueryDownValidByIdsModel;
import com.facishare.crm.stock.predefine.service.model.QueryUpValidWarehouseModel;
import com.facishare.crm.stock.predefine.service.model.WareHouseDetailModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by linchf on 2018/1/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class WareHouseServiceTest extends BaseServiceTest {
    @Autowired
    private WareHouseService wareHouseService;

    public WareHouseServiceTest() {
        super(WarehouseConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testQueryUpValid() throws MetadataServiceException {
        QueryUpValidWarehouseModel.Arg arg = new QueryUpValidWarehouseModel.Arg();
        arg.setAccountId("49679d95803c418599a3aba91ea27ec9");
        System.out.println(wareHouseService.queryUpValid(newServiceContext(), arg));
    }

    @Test
    public void testQueryDownValid() {
        WareHouseDetailModel.Result result = new WareHouseDetailModel.Result();

        ServiceContext serviceContext = newServiceContext();
        String productId = "df2274532dea406ab0ddc7ca9ddfc4ce";
        String accountId = "ad71b92b6f3b4efa956d4e6ca0c3b624";
        WareHouseDetailModel.Arg arg = new WareHouseDetailModel.Arg();
        arg.setProductId(productId);
        arg.setAccountId(accountId);

        result = wareHouseService.queryDownValid(serviceContext, arg);

        System.out.println("--------------------------------");
        System.out.println(result);
        System.out.println("--------------------------------");
    }

    @Test
    public void testQueryDownValidByIds() {
        QueryDownValidByIdsModel.Result result = new QueryDownValidByIdsModel.Result();
        QueryDownValidByIdsModel.Arg arg = new QueryDownValidByIdsModel.Arg();

        ServiceContext serviceContext = newServiceContext();
        List<String> productIds = Arrays.asList("f091909e263e4806bfad0cd727dceca9", "861bec14f7414c6b8804fa33d002e4d6", "65347dde742e48e8bd5feae3a3de06e1");
        String accountId = "e832eccbfe4b4a069d5197165fd6bd49";
        arg.setAccountId(accountId);
        arg.setProductIds(productIds);

        result = wareHouseService.queryDownValidByIds(serviceContext, arg);

        System.out.println("--------------------------------");
        System.out.println(result);
        System.out.println("--------------------------------");
    }

}

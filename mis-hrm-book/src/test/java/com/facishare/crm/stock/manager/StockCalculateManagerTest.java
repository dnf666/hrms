package com.facishare.crm.stock.manager;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.StockCalculateManager;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author liangk
 * @date 08/02/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockCalculateManagerTest {
    @Resource
    private StockCalculateManager stockCalculateManager;

    public StockCalculateManagerTest() {}

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testDeliveryNoteToInvalid() {
        User user = new User("55985", "1000");
        String id = "5a659bcf830bdbac278740a6";
        String warehouseId = "5a659bcf830bdbac278740a6";
        String salesId = "47fed06e41534a7093390a33178f0262";

        Map<String, BigDecimal> deliveryProduct = Maps.newHashMap();

        deliveryProduct.put("1e265f44575a4aedaf9d2d2f92218471", BigDecimal.TEN);
        deliveryProduct.put("f091909e263e4806bfad0cd727dceca9", BigDecimal.TEN);
        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(id).operateType(StockOperateTypeEnum.ADD.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                .operateResult(StockOperateResultEnum.PASS.value).build();

        stockCalculateManager.deliveryNoteToInvalid(user, warehouseId, salesId, deliveryProduct, stockOperateInfo);
    }

    @Test
    public void testDeliveryNoteToNormal() {
        User user = new User("55985", "1000");
        String id = "5a659bcf830bdbac278740a6";
        String warehouseId = "5a659bcf830bdbac278740a6";
        String salesId = "47fed06e41534a7093390a33178f0262";

        Map<String, BigDecimal> deliveryProduct = Maps.newHashMap();

        deliveryProduct.put("1e265f44575a4aedaf9d2d2f92218471", BigDecimal.TEN);
        deliveryProduct.put("f091909e263e4806bfad0cd727dceca9", BigDecimal.TEN);

        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(id).operateType(StockOperateTypeEnum.ADD.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                .operateResult(StockOperateResultEnum.PASS.value).build();

        stockCalculateManager.deliveryNoteToNormal(user, warehouseId, salesId, deliveryProduct, stockOperateInfo);
    }
}

package com.facishare.crm.outbounddeliverynote.manager;

import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteProductVO;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteVO;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteInitManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class OutboundDeliveryNoteManagerTest {
    @Resource
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void testCreate() {
        OutboundDeliveryNoteVO outboundDeliveryNoteVO = OutboundDeliveryNoteVO.builder().deliveryNoteId("5a794f36830bdbf133244819").outboundDate(Long.valueOf("1519833600000")).outboundType("5").warehouseId("5a72875f830bdbb849ef1025").build();
        OutboundDeliveryNoteProductVO outboundDeliveryNoteProductVO = OutboundDeliveryNoteProductVO.builder().stockId("5a79593f830bdbfc732ac719").productId("9f79f98dc479405b930932d247fb2adc").outboundAmount("1.00").build();

        outboundDeliveryNoteManager.create(new User("55983", "1000"), outboundDeliveryNoteVO, Arrays.asList(outboundDeliveryNoteProductVO), OutboundTypeEnum.SALES_OUTBOUND.value);
    }

    @Test
    public void testInvalid() {
        outboundDeliveryNoteManager.invalid(new User("55983", "1000"), "5a794f36830bdbf133244819", OutboundTypeEnum.SALES_OUTBOUND.value);
    }

    @Test
    public void testFindByIds() {
        outboundDeliveryNoteManager.findByIds(new User("55983", "1000"), Arrays.asList("5ab233a8830bdb61a8878ef0"));
    }
}

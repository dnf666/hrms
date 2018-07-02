package com.facishare.crm.deliverynote.manager.order;

import com.facishare.crm.deliverynote.predefine.manager.order.OrderManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class OrderManagerTest {

    @Test
    public void orderNotify_Sucess() {
        OrderManager orderManager = new OrderManager();
        OrderManager.OrderNotifyArg arg = OrderManager.OrderNotifyArg.builder()
                .orderId("6ad1bb4e4a6045e9bb0fd2d0be65d891")
                .typeobjectid("20180201_41")
                .typeobjectid("3")
                .tenantId("55988").build();

        OrderManager.OrderNotifyResult result = orderManager.orderNotify(arg);
        log.info("result [{}]", result);

    }

}

package com.facishare.crm.customeraccount;

import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujf on 2018/1/31.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrepayDetailManagerTest {

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Autowired
    PrepayDetailManager prepayDetailManager;

    @Test
    public void queryByOrderPaymentId() {
        User user = new User("59768", "1000");
        IObjectData prepayObjectData = prepayDetailManager.getByOrderPaymentId(user, "5a716323a5083d4f88325340");
        log.debug("prepayObjectData==>{}", prepayObjectData);
    }

    @Autowired
    public void listInvalidOrderPaymentIds() {
        List<String> orderPaymentIds = new ArrayList<>();
        //FIXME 
        //        orderPaymentIds.add()
        //        User user = new User("59768", "1000");
        //        prepayDetailManager.listInvalidDataByOrderPaymentIds();
    }

}

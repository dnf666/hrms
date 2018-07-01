package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class ElecSignConfigManagerTest {

    @Resource
    private ElecSignConfigManager elecSignConfigManager;

    @Test
    public void updateAppElecSignStatus_Success() {
        User user = new User("55985", User.SUPPER_ADMIN_USER_ID);
        elecSignConfigManager.updateAppElecSignStatus(user, AppTypeEnum.DING_HUO_TONG, AppElecSignSwitchEnum.ON);
        AppElecSignSwitchEnum appSwitch = elecSignConfigManager.getAppElecSignStatus(user.getTenantId(), AppTypeEnum.DING_HUO_TONG);
        Assert.assertEquals(appSwitch, AppElecSignSwitchEnum.ON);

        elecSignConfigManager.updateAppElecSignStatus(user, AppTypeEnum.DING_HUO_TONG, AppElecSignSwitchEnum.OFF);
        appSwitch = elecSignConfigManager.getAppElecSignStatus(user.getTenantId(), AppTypeEnum.DING_HUO_TONG);
        Assert.assertEquals(appSwitch, AppElecSignSwitchEnum.OFF);
    }
}

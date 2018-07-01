package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.type.SwitchTypeEnum;
import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by chenzs on 2018/5/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class ElecSignServiceTest extends BaseServiceTest{
    @Resource
    private ElecSignService elecSignService;

    public ElecSignServiceTest() {
        super(InternalSignCertifyObjConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Override
    public void initUser() {
        this.tenantId = "55988";
        this.fsUserId = "1000";
    }

    @Test
    public void changeInitStatusNotOpenTest() {
        String pro = System.getProperty("spring.profiles.active");
        ElecSignType.InitElecSign.Result result = elecSignService.changeInitStatusNotOpen(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void changeInitStatusOpenedTest() {
        ElecSignType.InitElecSign.Result result = elecSignService.changeInitStatusOpened(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void initElecSignTest() {
        String pro = System.getProperty("spring.profiles.active");
        ElecSignType.InitElecSign.Result result = elecSignService.initElecSign(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void isUseCustomAccountStatementObjApiNameTest() {
        String pro = System.getProperty("spring.profiles.active");
        ElecSignType.InitElecSign.Result result = elecSignService.isUseCustomAccountStatementObjApiName(newServiceContext());
        System.out.println(result);
    }

    @Test
    public void enableOrDisableTenantSwitch() {
        ElecSignType.EnableOrDisableTenantSwitch.Arg arg = new ElecSignType.EnableOrDisableTenantSwitch.Arg();
        arg.setSwitchType(SwitchTypeEnum.TENANT_ELECTRONIC_SIGN.getType());
        arg.setStatus(1);
        ElecSignType.EnableOrDisableTenantSwitch.Result result = elecSignService.enableOrDisableTenantSwitch(newServiceContext(), arg);
        System.out.println(result);
    }
}

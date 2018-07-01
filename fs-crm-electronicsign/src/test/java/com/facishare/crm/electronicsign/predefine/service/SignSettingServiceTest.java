package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.dao.SignSettingDAO;
import com.facishare.crm.electronicsign.predefine.manager.SignSettingManager;
import com.facishare.crm.electronicsign.predefine.manager.SignerManager;
import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignSettingVO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignerSettingVO;
import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class SignSettingServiceTest extends BaseServiceTest {
    @Resource
    private SignSettingService signSettingService;
    @Resource
    private SignSettingManager signSettingManager;
    @Resource
    private SignSettingDAO signSettingDAO;

    public SignSettingServiceTest() {
        super("");
    }

    @Override
    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = "1000";
    }

    @Test
    public void enableSwitchForApp_Success() {
        ElecSignType.EnableSwitchForApp.Arg enableAppArg = new ElecSignType.EnableSwitchForApp.Arg();
        enableAppArg.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        enableAppArg.setStatus(AppElecSignSwitchEnum.OFF.getStatus());
        ElecSignType.EnableSwitchForApp.Result enableAPPResult = signSettingService.enableSwitchForApp(this.newServiceContext(), enableAppArg);
        Assert.assertEquals(enableAPPResult.getStatus(), 1);

        ElecSignType.QueryAppSwitchAndSignSetting.Arg arg = new ElecSignType.QueryAppSwitchAndSignSetting.Arg();
        arg.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        ElecSignType.QueryAppSwitchAndSignSetting.Result result = signSettingService.queryAppSwitchAndSignSetting(this.newServiceContext(), arg);
        Assert.assertEquals(result.getAppSwitch(), AppElecSignSwitchEnum.OFF.getStatus());


        enableAppArg = new ElecSignType.EnableSwitchForApp.Arg();
        enableAppArg.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        enableAppArg.setStatus(AppElecSignSwitchEnum.ON.getStatus());
        enableAPPResult = signSettingService.enableSwitchForApp(this.newServiceContext(), enableAppArg);
        Assert.assertEquals(enableAPPResult.getStatus(), 1);
        result = signSettingService.queryAppSwitchAndSignSetting(this.newServiceContext(), arg);
        Assert.assertEquals(result.getAppSwitch(), AppElecSignSwitchEnum.ON.getStatus());
    }


    @Test
    public void saveUpdateSignSetting_Success() {
        ElecSignType.SaveOrUpdateSignSetting.Arg arg = new  ElecSignType.SaveOrUpdateSignSetting.Arg();
        arg.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        arg.setIsHasOrder(true);
        arg.setObjApiName("SalesOrderObj");

        SignerSettingVO signerSettingVO = new SignerSettingVO();
        signerSettingVO.setKeyword("haha");
        signerSettingVO.setOrderNum(1);
        signerSettingVO.setSignType(SignTypeEnum.By_Hand.getType());
        arg.setSignerSettings(Lists.newArrayList(signerSettingVO));

        ElecSignType.SaveOrUpdateSignSetting.Result result = signSettingService.saveOrUpdateSignSetting(this.newServiceContext(), arg);
        Assert.assertTrue(result.getStatus() == 1);

        signerSettingVO.setKeyword("haha2");
        SignerSettingVO signerSettingVO2 = new SignerSettingVO();
        signerSettingVO2.setKeyword("hehe");
        signerSettingVO2.setOrderNum(2);
        signerSettingVO2.setSignType(SignTypeEnum.Auto.getType());
        arg.getSignerSettings().add(signerSettingVO2);

        result = signSettingService.saveOrUpdateSignSetting(this.newServiceContext(), arg);
        Assert.assertTrue(result.getStatus() == 1);
    }

    @Test
    public void deleteSignSetting_Success() {
        SignSettingDO signSettingDO = new SignSettingDO();
        signSettingDO.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        signSettingDO.setTenantId(this.user.getTenantId());
        signSettingDO.setObjApiName("SalesOrderObj");
        signSettingDO.setIsHasOrder(true);
        signSettingDAO.createOrUpdate(signSettingDO);

        List<SignSettingDO> queryList = signSettingDAO.queryList(signSettingDO);
        Assert.assertTrue(queryList.size() == 1);

        signSettingDO = new SignSettingDO();
        signSettingDO.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        signSettingDO.setTenantId(this.user.getTenantId());
        signSettingDO.setObjApiName("SalesOrderObj");
        signSettingDO.setIsHasOrder(true);
        signSettingDAO.createOrUpdate(signSettingDO);

        queryList = signSettingDAO.queryList(signSettingDO);
        Assert.assertTrue(queryList.size() == 1);


        ElecSignType.DeleteSignSetting.Arg arg = new  ElecSignType.DeleteSignSetting.Arg();
        arg.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        arg.setSignSettingId(queryList.get(0).getId());
        signSettingService.deleteSignSetting(this.newServiceContext(), arg);

        queryList = signSettingDAO.queryList(signSettingDO);
        Assert.assertTrue(queryList.size() == 0);
    }

}

package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import com.facishare.crm.electronicsign.predefine.dao.SignSettingDAO;
import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.facishare.crm.electronicsign.predefine.model.SignerSettingDO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/test-mongo.xml")
@Slf4j
public class SignSettingDAOTest {

    @Resource
    private SignSettingDAO signSettingDAO;

    @Test
    public void queryList_Success() {
        String tenantId = "asdf";
        AppTypeEnum appTypeEnum = AppTypeEnum.DING_HUO_TONG;

        SignSettingDO signSettingDO = new SignSettingDO();
        signSettingDO.setTenantId(tenantId);
        signSettingDO.setAppType(appTypeEnum.getType());
        signSettingDO.setIsHasOrder(true);
        signSettingDO.setObjApiName(DeliveryNoteObjConstants.API_NAME);

        SignerSettingDO signerSettingDO = new SignerSettingDO();
        signerSettingDO.setKeyword("haha");
        signSettingDO.setSignerSettings(Lists.newArrayList(signerSettingDO));

        signSettingDAO.save(signSettingDO);

        SignSettingDO queryCondition = new SignSettingDO();
        queryCondition.setTenantId(tenantId);
        queryCondition.setAppType(appTypeEnum.getType());
        List<SignSettingDO> queryResult = signSettingDAO.queryList(queryCondition);
        Assert.assertTrue(queryResult.size() == 1);
        Assert.assertEquals(queryResult.get(0).getTenantId(), tenantId);
        Assert.assertEquals(queryResult.get(0).getAppType(), appTypeEnum.getType());

        Assert.assertTrue(queryResult.get(0).getSignerSettings().size() == 1);
        SignerSettingDO querySignerSetting = queryResult.get(0).getSignerSettings().get(0);
        Assert.assertEquals(querySignerSetting.getKeyword(), signerSettingDO.getKeyword());
    }

    @Test
    public void createOrUpdate_Success() {
        SignSettingDO signSettingDO = new SignSettingDO();
        signSettingDO.setAppType(AppTypeEnum.DING_HUO_TONG.getType());
        signSettingDO.setTenantId("55983");
        signSettingDO.setObjApiName("SalesOrderObj");
        signSettingDO.setIsHasOrder(true);
        SignerSettingDO signerSettingDO = new SignerSettingDO();
        signerSettingDO.setKeyword("haha");
        signerSettingDO.setOrderNum(1);
        signerSettingDO.setSignType(SignTypeEnum.By_Hand.getType());
        signSettingDO.setSignerSettings(Lists.newArrayList(signerSettingDO));
        signSettingDAO.createOrUpdate(signSettingDO);


        List<SignSettingDO> queryList = signSettingDAO.queryList(signSettingDO);
        Assert.assertEquals(queryList.size(), 1);
        SignSettingDO queryResult = queryList.get(0);
        Assert.assertEquals(signSettingDO.getObjApiName(), queryResult.getObjApiName());
        Assert.assertEquals(signSettingDO.getTenantId(), queryResult.getTenantId());
        Assert.assertEquals(signSettingDO.getSignerSettings(), queryResult.getSignerSettings());
        Assert.assertEquals(signSettingDO.getAppType(), queryResult.getAppType());

        signSettingDO.getSignerSettings().get(0).setKeyword("haha2");
        SignerSettingDO signerSettingDO2 = new SignerSettingDO();
        signerSettingDO2.setKeyword("hehe");
        signerSettingDO2.setOrderNum(2);
        signerSettingDO2.setSignType(SignTypeEnum.Auto.getType());
        signSettingDO.getSignerSettings().add(signerSettingDO2);

        signSettingDAO.createOrUpdate(signSettingDO);

        SignSettingDO queryCondition = new SignSettingDO();
        queryCondition.setObjApiName(signSettingDO.getObjApiName());
        List<SignSettingDO> queryListResult = signSettingDAO.queryList(queryCondition);
        Assert.assertTrue(queryListResult.size() == 1);

        queryResult = queryListResult.get(0);
        Assert.assertTrue(queryResult.getSignerSettings().size() == 2);
        Assert.assertEquals(queryResult.getSignerSettings().get(0).getKeyword(), "haha2");
        Assert.assertEquals(queryResult.getSignerSettings().get(1).getKeyword(), "hehe");

    }

}

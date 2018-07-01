package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.service.dto.InternalSignCertifyUseRangeType;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class InternalSignCertifyUseRangeServiceTest extends BaseServiceTest {

    static {
        System.setProperty("process.profile", "ceshi113");
    }

    @Resource
    private InternalSignCertifyUseRangeService internalSignCertifyUseRangeService;

    public InternalSignCertifyUseRangeServiceTest() {
        super("");
    }

    @Override
    public void initUser() {
        this.tenantId = "53409";
        this.fsUserId = "1000";
    }

    @Test
    public void setUseRange_Success() {
        InternalSignCertifyUseRangeType.SetUseRange.Arg arg = new InternalSignCertifyUseRangeType.SetUseRange.Arg();
        Map<String, List<String>> argMap = Maps.newHashMap();
        argMap.put("5afcf5dfbab09cacbf3404c7", Lists.newArrayList("1011", "1012"));
        argMap.put("5afcf381bab09ca875654321", Lists.newArrayList("1013"));
        argMap.put("5afc0fdebab09c65776cbf9d", null);
        arg.setSettingMap(argMap);

        internalSignCertifyUseRangeService.setUseRange(this.newServiceContext(), arg);

        InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.Result result = internalSignCertifyUseRangeService.getInternalSignCertifyUseRangeSettingList(newServiceContext());
        Assert.assertTrue(result.getSignAccountDataList().size() == 3);

        Map<String, InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.SignAccountData> signAccountMap = result.getSignAccountDataList().stream().collect(Collectors.toMap(signAccountData -> signAccountData.getInternalSignCertifyId(), Function.identity()));

        argMap.forEach((key, value) -> {
            InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.SignAccountData querySignAccountData = signAccountMap.get(key);
            List<String> rangDeptIds = querySignAccountData.getInternalSignCertifyUseRange().getDepartmentIds();
            if (CollectionUtils.isEmpty(value)) {
                Assert.assertTrue(CollectionUtils.isEmpty(rangDeptIds));
            } else {
                Assert.assertTrue(value.size() == rangDeptIds.size());
                value.forEach(departmentId -> {
                    Assert.assertTrue(rangDeptIds.contains(departmentId));
                });
            }
        });
    }

    @Test(expected = ValidateException.class)
    public void setUseRange_throws_ValidateException_for_arg_repeat_department() {
        InternalSignCertifyUseRangeType.SetUseRange.Arg arg = new InternalSignCertifyUseRangeType.SetUseRange.Arg();
        Map<String, List<String>> argMap = Maps.newHashMap();
        argMap = Maps.newHashMap();
        argMap.put("5afcf5dfbab09cacbf3404c7", Lists.newArrayList("1011", "1012"));
        argMap.put("5afcf381bab09ca875654321", Lists.newArrayList("1013"));
        argMap.put("5afc0fdebab09c65776cbf9d", Lists.newArrayList("1013"));
        arg.setSettingMap(argMap);

        internalSignCertifyUseRangeService.setUseRange(this.newServiceContext(), arg);
    }

    @Test(expected = ValidateException.class)
    public void setUseRange_throws_ValidateException_for_has_repeat_department() {
        InternalSignCertifyUseRangeType.SetUseRange.Arg arg = new InternalSignCertifyUseRangeType.SetUseRange.Arg();
        Map<String, List<String>> argMap = Maps.newHashMap();
        argMap = Maps.newHashMap();
        argMap.put("5afcf5dfbab09cacbf3404c7", Lists.newArrayList("1011", "1012"));
        arg.setSettingMap(argMap);
        internalSignCertifyUseRangeService.setUseRange(this.newServiceContext(), arg);

        arg = new InternalSignCertifyUseRangeType.SetUseRange.Arg();
        argMap = Maps.newHashMap();
        argMap.put("5afcf381bab09ca875654321", Lists.newArrayList("1011", "1012"));
        arg.setSettingMap(argMap);
        internalSignCertifyUseRangeService.setUseRange(this.newServiceContext(), arg);
    }

}

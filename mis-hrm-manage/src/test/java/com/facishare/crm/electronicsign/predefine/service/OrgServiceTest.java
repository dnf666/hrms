package com.facishare.crm.electronicsign.predefine.service;


import com.facishare.paas.appframework.common.service.OrgService;
import com.facishare.paas.appframework.common.service.dto.GetNDeptPathByUserId;
import com.facishare.paas.appframework.common.service.dto.QueryAllSuperDeptsByDeptIds;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
    public class OrgServiceTest {
    @Resource
    private OrgService orgService;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void getAllSuperDeptsByDeptIds_Success() {
        User user = new User("55983", User.SUPPER_ADMIN_USER_ID);

        Map<String, List<QueryAllSuperDeptsByDeptIds.DeptInfo>> deptMap = orgService.getAllSuperDeptsByDeptIds("55983", User.SUPPER_ADMIN_USER_ID,  Lists.newArrayList("1008"));
        log.info("{}", deptMap);
    }

    @Test
    public void getNDeptPathByUserId_Success() {
        User user = new User("55983", "1002");
        // 部门层级理论上不会有100000多级的正常企业
        List<GetNDeptPathByUserId.DeptInfo> deptInfoList = orgService.getNDeptPathByUserId(user, user.getUserId(),  100000);
        log.info("{}", deptInfoList);
    }
}

package com.mis.hrm.web.manage.controller;

import com.mis.hrm.manage.model.Management;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * created on 2019-03-01
 *
 * @author dailinfu
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
public class ManageControllerTest {
    @Resource
    private ManageController manageController;
    private  Logger logger=LoggerFactory.getLogger(this.getClass());

    @Test
    public void insertOneManage() {
        Management management = Management.builder().companyId("123").email("123").password("123").permission(2).build();
        manageController.insertOneManage(management);
    }

    @Test
    public void updatePasswordOrPermission() {
        Management management = Management.builder().companyId("123").email("123").password("213").permission(1).build();
        manageController.updatePasswordOrPermission(management);
    }
    @Test
    public void find() {
        Management management = Management.builder().companyId("123").email("123").password("213").permission(1).build();
        System.out.println(manageController.findManagementByCompanyIdAndEmail(management));
    }
}
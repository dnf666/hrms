package com.mis.hrm.web.project.controller;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * created by dailf on 2019-01-27
 *
 * @author dailf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
public class ProjectControllerTest {
    @Resource
    private ProjectController projectController;
    @Test
    public void insertProject() {
    }

    @Test
    public void deleteProjectByCompanyIdAndProjectId() {
    }

    @Test
    public void updateProjectBycompanyIdAndProjectId() {
    }

    @Test
    public void deleteByProjectIds() {
    }

    @Test
    public void getProjectCount() {
    }

    @Test
    public void searchProject() {
    }

    @Test
    public void infoMember() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberEmails","1204695257@qq.com,1589056125@qq.com");
        projectController.infoMember(jsonObject,"1204695257@qq.com",27);

    }
}
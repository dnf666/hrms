package com.mis.hrm.web.project.controller;

import com.mis.hrm.project.po.Project;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/9/19
 *
 * @author dailf
 */
@RunWith(SpringRunner.class)
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
    public void getProjectCount() {
    }

    @Test
    public void searchProject() {
        Project project = Project.builder().projectId(2)
                .companyId("1204695257@qq.com")
                .build();

        System.out.println(projectController.searchProject(project,1,10));
    }
}
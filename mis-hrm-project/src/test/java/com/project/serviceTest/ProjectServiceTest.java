package com.project.serviceTest;

import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.service.ProjectService;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-project-test.xml")
public class ProjectServiceTest {
    private Project project;
    @Autowired
    private ProjectService projectService;

    @Before
    public void setUp() throws Exception {
        project = Project.builder()
                .companyId("123")
                .projectUrl("baidu.com")
                .projectId(324)
                .projectName("lalala")
                .onlineTime("2018-12-21").build();
    }

    @Test
    public void deleteByPrimaryKey(){
        project.setProjectId(13);
        project.setCompanyId("xinguan");
        try {
            Assert.assertEquals(1, projectService.deleteByPrimaryKey(project));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        String message = "";

        project.setCompanyId(" ");
        try {
            projectService.deleteByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("公司id为空", message);

        project = null;
        try {
            projectService.deleteByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            message = n.getMessage();
        }
        Assert.assertEquals("删除的prject对象为空", message);
    }

    @Test
    public void insert(){
        try {
            Assert.assertEquals(1, projectService.insert(project));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }

        String msg = "";
        project.setProjectName(" ");
        try {
            projectService.insert(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("插入的数据项条件缺失", msg);

        project = null;
        try {
            projectService.insert(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("删除的prject对象为空", msg);
    }

    @Test
    public void selectByPrimaryKey(){
        project.setProjectId(12);
        project.setCompanyId("xinguan");
        try {
            Assert.assertEquals(1, projectService.deleteByPrimaryKey(project));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }

        String message = "";

        project.setCompanyId(" ");
        try {
            projectService.selectByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("公司id为空", message);

        project = null;
        try {
            projectService.selectByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            message = n.getMessage();
        }
        Assert.assertEquals("传入的prject对象为空", message);
    }

    @Test
    public void updateByPrimaryKey(){
        project.setProjectId(14);
        project.setCompanyId("xinguan");
        project.setProjectName("dddd");
        try {
            Assert.assertEquals(1, projectService.updateByPrimaryKey(project));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        String message = "";

        project.setCompanyId(" ");
        try {
            projectService.updateByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("公司id为空", message);

        project = null;
        try {
            projectService.updateByPrimaryKey(project);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            message = n.getMessage();
        }
        Assert.assertEquals("传入的prject对象为空", message);

    }
}

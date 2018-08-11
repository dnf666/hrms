package com.project.daoTest;

import com.alibaba.fastjson.JSON;
import com.mis.hrm.project.dao.ProjectMapper;
import com.mis.hrm.project.po.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-project-test.xml")
public class ProjectDao {
    private Project project;
    @Autowired
    private ProjectMapper projectMapper;

    @Before
    public void setUp() throws Exception {
        project = Project.builder()
                .companyId("lalalala")
                .projectId(12)
                .projectUrl("不晓得")
                .onlineTime(LocalDate.now().toString()).build();
    }

    @Test
    public void getProjectJson(){
        String jsonProject = JSON.toJSONString(project);
        System.out.println(jsonProject);
    }

    @Test
    public void delete(){
        int expectNum = 1;
        project.setCompanyId("xinguan");
        project.setProjectId(12);
        int result = projectMapper.deleteByPrimaryKey(project);
        Assert.assertEquals(expectNum, result);
    }

    @Test
    public void insert(){
        int expectNum = 1;
        int result = projectMapper.insert(project);
        Assert.assertEquals(expectNum, result);
    }

    @Test
    public void selectById(){
        project.setCompanyId("xinguan");
        project.setProjectId(12);
        String expectResult = "xinguanba ";
        String result = projectMapper.selectByPrimaryKey(project).getProjectName();
        Assert.assertEquals(expectResult, result);
    }


    @Test
    public void updateById(){
        project.setCompanyId("xinguan");
        project.setProjectId(12);
        project.setProjectName("lalallalalalala");
        int expectNum = 1;
        int result = projectMapper.updateByPrimaryKey(project);
        Assert.assertEquals(expectNum, result);
    }
}

package com.mis.hrm.project.service.impl;

import com.mis.hrm.project.po.Project;
import org.junit.Test;

/**
 * created on 2019-04-24
 *
 * @author dailinfu
 */

public class ProjectServiceImplTest {

    @Test
    public void listProjectInTomcat() throws Exception {
        ProjectServiceImpl projectService = new ProjectServiceImpl();
        Project project = new Project();
        project.setIp("172.18.73.244");
        project.setPorts("8089");
        System.out.println(projectService.listProjectInTomcat(project));
    }
}

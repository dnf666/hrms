package com.mis.hrm.project.service;

import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.po.Webapp;
import com.mis.hrm.util.BaseService;

import java.util.List;

public interface ProjectService extends BaseService<Project> {
    int getProjectCount(Project project);

    Integer deleteByProjectIds(List<Integer> numList, String companyId);

    boolean infoMember(List<String> emailList, String companyId, Integer projectId) throws Exception;

    List<Webapp> listProjectInTomcat(Project project) throws Exception;

    boolean operateProject(Project project, String operation) throws Exception;
}

package com.mis.hrm.project.service;

import com.mis.hrm.project.po.Heart;
import com.mis.hrm.project.po.Project;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;

import java.util.List;

public interface ProjectService extends BaseService<Project> {
    int getProjectCount(Project project);

    Integer deleteByProjectIds(List<Integer> numList, String companyId);

}

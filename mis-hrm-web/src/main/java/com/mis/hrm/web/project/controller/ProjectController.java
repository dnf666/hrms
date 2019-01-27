package com.mis.hrm.web.project.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.service.ProjectService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.constant.PageConstant;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.web.util.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("project")
    public Map insertProject(@RequestBody Project project) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::insert, project);
        return result;
    }

    @PostMapping("delProject")
    public Map deleteProjectByCompanyIdAndProjectId(@RequestBody Project project) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::deleteByPrimaryKey, project);
        return result;
    }

    @PutMapping("project")
    public Map updateProjectBycompanyIdAndProjectId(@RequestBody Project project) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::updateByPrimaryKey, project);
        return result;
    }
    @PostMapping("delProjects")
    public Map deleteByProjectIds(@RequestBody JSONObject jsonObject, @RequestParam("companyId") String companyId) {
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("projectIds").toString());
        Map<String, Object> map;
        List<Integer> numList = jsonArray.toJavaList(Integer.class);
        try {
            map = ToMap.toSuccessMap(projectService.deleteByProjectIds(numList, companyId));
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e) {
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("count")
    public Map getProjectCount(Project project) {
        Map<String, Object> result;
        result = ToMap.toSuccessMap(projectService.getProjectCount(project));
        return result;
    }

    @PostMapping("option")
    public Map searchProject(@RequestBody Project project, Integer currentPage, Integer size) {
        if (Strings.isNullOrEmpty(project.getCompanyId())) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        if (currentPage == null) {
            currentPage = PageConstant.DEFUALT_PAGE;
        }
        if (size == null) {
            size = PageConstant.DEFUALT_SIZE;
        }
        Pager<Project> pager = new Pager<>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(size);
        List<Project> projectList = projectService.selectByPrimaryKeyAndPage(project, pager);
        pager.setData(projectList);
        return ToMap.toSuccessMap(pager);
    }
    @PostMapping("info")
    public Map infoMember(@RequestBody JSONObject jsonObject,@RequestParam("companyId") String companyId,@RequestParam("projectId") Integer projectId){
        String memberEmails = jsonObject.get("memberEmails").toString();
        String emailArray[] = memberEmails.split(",");
        List<String> emailList = Arrays.asList(emailArray);
        try {
         boolean result = projectService.infoMember(emailList,companyId,projectId);
         return ToMap.toSuccessMap(result);
        }catch (Exception e){
           return ToMap.toFalseMap(e.getMessage());
        }


    }
}

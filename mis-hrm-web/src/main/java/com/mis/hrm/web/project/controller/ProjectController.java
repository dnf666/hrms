package com.mis.hrm.web.project.controller;

import com.google.common.base.Strings;
import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.service.ProjectService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.constant.PageConstant;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.web.util.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    /**
     * @api {POST} project 插入一项目的信息
     * @apiDescription 插入一个项目的信息
     * @apiGroup PROJECT-ADD
     * @apiParam {String} companyId 公司id
     * @apiParam {String} projectId　项目id
     * @apiParam {String} projectName 项目名称
     * @apiParam {String} projectUrl　项目地址
     * @apiParam {String} onlineTime　在线时间
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "code": "1",
     * "msg": "success"
     * "object": null
     * }
     */
    @PostMapping("project")
    public Map insertProject(Project project) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::insert, project);
        return result;
    }

    /**
     * @api {DELETE} project/{companyId}/{projectId} 通过companyId & projectId
     * @apiDescription 通过companyId & projectId删除一个项目的信息
     * @apiGroup PROJECT-DELETE
     * @apiParam {String} companyId 公司id
     * @apiParam {String} projectId　项目id
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "code": "1",
     * "msg": "success"
     * "object": null
     * }
     */
    @DeleteMapping("{companyId}/{projectId}")
    public Map deleteProjectByCompanyIdAndProjectId(@PathVariable("companyId") String companyId,
                                                    @PathVariable("projectId") int projectId) {
        Project project = Project.builder().companyId(companyId).projectId(projectId).build();
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::deleteByPrimaryKey, project);
        return result;
    }

    /**
     * @api {PUT} project 通过companyId & projectI更新项目的信息
     * @apiDescription 通过companyId & projectId更新项目的信息，同时返回更新后的信息
     * @apiGroup PROJECT-UPDATE
     * @apiParam {String} companyId 公司id
     * @apiParam {String} projectId　项目id
     * @apiParam {String} projectName 项目名称
     * @apiParam {String} projectUrl　项目地址
     * @apiParam {String} onlineTime　在线时间
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "code": "1",
     * "msg": "success"
     * "object":{
     * "companyId": "lalalala",
     * "onlineTime": "2018-08-08",
     * "projectId": 12,
     * "projectUrl": "不晓得"
     * }
     * }
     */
    @PutMapping("project")
    public Map updateProjectBycompanyIdAndProjectId(Project project) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(projectService::updateByPrimaryKey, project);
        return result;
    }

    /**
     * @api {GET} project 通过companyId & projectId得到项目的信息
     * @apiDescription 通过companyId & projectId得到项目的信息
     * @apiGroup PROJECT-QUERY
     * @apiParam {String} companyId 公司id
     * @apiParam {String} projectId　项目id
     * @apiParam {String} projectName 项目名称
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "code": "1",
     * "msg": "success"
     * "object":[{
     * "companyId": "lalalala",
     * "onlineTime": "2018-08-08",
     * "projectId": 12,
     * "projectUrl": "不晓得"
     * },{
     * "companyId": "lalalala",
     * "onlineTime": "2018-08-08",
     * "projectId": 12,
     * "projectUrl": "不晓得"
     * }
     * }
     * ]
     */
//    @GetMapping("project")
//    public Map getProjectBycompanyIdAndProjectId(Project project){
//        Map<String, Object> result;
//        result = ControllerUtil.getResult(projectService::selectByPrimaryKey, project);
//        return result;
//    }
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
}

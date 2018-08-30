package com.mis.hrm.web.project.controller;

import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

public class ProjectController {

    /**
     *   @api {POST} project 插入一项目的信息
     *   @apiDescription 插入一个项目的信息
     *   @apiGroup PROJECT-ADD
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} projectId　项目id
     *   @apiParam  {String} projectName 项目名称
     *   @apiParam  {String} projectUrl　项目地址
     *   @apiParam  {String} onlineTime　在线时间
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    @PostMapping("project")
    public Map insertProject(Project project){

        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, null);
    }

    /**
     *   @api {DELETE} project 通过companyId & projectId
     *   @apiDescription 通过companyId & projectId删除一个项目的信息
     *   @apiGroup PROJECT-DELETE
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} projectId　项目id
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    @DeleteMapping("project")
    public Map deleteProjectByCompanyIdAndProjectId(Project project){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS,null);
    }

    /**
     *   @api {PUT} project 通过companyId & projectId更新项目的信息
     *   @apiDescription 通过companyId & projectId更新项目的信息，同时返回更新后的信息
     *   @apiGroup PROJECT-UPDATE
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} projectId　项目id
     *   @apiParam  {String} projectName 项目名称
     *   @apiParam  {String} projectUrl　项目地址
     *   @apiParam  {String} onlineTime　在线时间
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":{
     * 	                "companyId": "lalalala",
     *              	"onlineTime": "2018-08-08",
     * 	                "projectId": 12,
     * 	                "projectUrl": "不晓得"
     *                  }
     *       }
     */
    @PutMapping("project")
    public Map updateProjectBycompanyIdAndProjectId(Project project){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS,Project.builder().build());
    }

    /**
     *   @api {GET} project 通过companyId & projectId得到项目的信息
     *   @apiDescription 通过companyId & projectId得到项目的信息
     *   @apiGroup PROJECT-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} projectId　项目id
     *   @apiParam  {String} projectName 项目名称
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     * 	                "companyId": "lalalala",
     *              	"onlineTime": "2018-08-08",
     * 	                "projectId": 12,
     * 	                "projectUrl": "不晓得"
     *                  },{
     *       	                "companyId": "lalalala",
     *                    	"onlineTime": "2018-08-08",
     *       	                "projectId": 12,
     *       	                "projectUrl": "不晓得"
     *                     }
     *          }
     *       ]
     */
    @GetMapping("project")
    public Map getProjectBycompanyIdAndProjectId(Project project){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS,Project.builder().build());
    }



}

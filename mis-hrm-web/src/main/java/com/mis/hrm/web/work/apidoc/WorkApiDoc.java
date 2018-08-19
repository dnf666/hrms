package com.mis.hrm.web.work.apidoc;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.work.model.Whereabout;

import java.util.ArrayList;
import java.util.Map;

public class WorkApiDoc {

    /**
     *   @api {POST} work 添加单个成员信息
     *   @apiGroup WORK-ADD
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} num 学号
     *   @apiParam  {String} name 姓名
     *   @apiParam  {String} phoneNumber 电话
     *   @apiParam  {String} email 邮箱
     *   @apiParam  {String} grade 年级（如2017级）
     *   @apiParam  {String} sex 性别
     *   @apiParam  {String} profession 专业
     *   @apiParam  {String} department 部门
     *   @apiParam  {String} workPlace 工作地点
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map insertOneWorker(Whereabout whereabout){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     *   @api {DELETE} work 删除单个成员信息
     *   @apiDescription 根据companyId和num删除成员信息
     *   @apiGroup WORK-DELETE
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} num 学号
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map deleteOneWorker(Whereabout whereabout){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);

    }

    /**
     *   @api {PUT} work 更新单个成员信息
     *   @apiDescription 根据companyId和num更新成员信息
     *   @apiGroup WORK-UPDATE
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} num 学号
     *   @apiParam  {String} name 姓名
     *   @apiParam  {String} phoneNumber 电话
     *   @apiParam  {String} email 邮箱
     *   @apiParam  {String} grade 年级（如2017级）
     *   @apiParam  {String} sex 性别
     *   @apiParam  {String} profession 专业
     *   @apiParam  {String} department 部门
     *   @apiParam  {String} workPlace 工作地点
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map updateOneWorker(Whereabout whereabout){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);

    }

    /**
     *   @api {GET} work 查找单个成员信息
     *   @apiDescription 根据companyId和num查找成员信息
     *   @apiGroup WORK-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} num 学号
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":{
     *             "companyId": "信管工作室",
     *             "num": "001",
     *             "name": "大红",
     *             "phoneNumber": "21212222222",
     *             "email": "211@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         }
     *       }
     */
    public Map selectOneWorker(String companyId, String num){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new Whereabout());
    }

    /**
     *   @api {GET} work/count 获取成员总数
     *   @apiDescription 直接返回成员总数
     *   @apiGroup WORK-QUERY
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": 12
     *       }
     */
    public Map countWorkers(){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,0);
    }

    /**
     *   @api {GET} work/byGrade/{page} 根据年级获取成员
     *   @apiDescription 根据年级的分页查询（注意这里没有模糊查询）
     *   @apiGroup WORK-QUERY
     *   @apiParam  {Integer} page 当前页码
     *   @apiParam  {String} grade 年级（如2017级）
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *             "companyId": "信管工作室",
     *             "num": "001",
     *             "name": "大红",
     *             "phoneNumber": "21212222222",
     *             "email": "211@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         },
     *         {
     *             "companyId": "信管工作室",
     *             "num": "002",
     *             "name": "大白",
     *             "phoneNumber": "21212333333",
     *             "email": "222@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         }]
     *       }
     */
    public Map findByGrade(String grade, Integer page, Pager<Whereabout> pager){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new ArrayList<>());
    }

    /**
     *   @api {GET} work/byName/{page} 根据姓名获取成员
     *   @apiDescription 根据姓名的模糊分页查询
     *   @apiGroup WORK-QUERY
     *   @apiParam  {Integer} page 当前页码
     *   @apiParam  {String} name 姓名
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *             "companyId": "信管工作室",
     *             "num": "001",
     *             "name": "大红",
     *             "phoneNumber": "21212222222",
     *             "email": "211@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         },
     *         {
     *             "companyId": "信管工作室",
     *             "num": "002",
     *             "name": "大白",
     *             "phoneNumber": "21212333333",
     *             "email": "222@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         }]
     *       }
     */
    public Map findByName( String name, Integer page, Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new ArrayList<>());
    }

    /**
     *   @api {GET} work/all/{page} 获取成员列表
     *   @apiDescription 分页获取全部成员信息
     *   @apiGroup WORK-QUERY
     *   @apiParam  {Integer} page 当前页码
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *             "companyId": "信管工作室",
     *             "num": "001",
     *             "name": "大红",
     *             "phoneNumber": "21212222222",
     *             "email": "211@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         },
     *         {
     *             "companyId": "信管工作室",
     *             "num": "002",
     *             "name": "大白",
     *             "phoneNumber": "21212333333",
     *             "email": "222@222.com",
     *             "grade": "2017级",
     *             "sex": "女",
     *             "profession": "信管",
     *             "department": "后台",
     *             "workPlace": "小米"
     *         }]
     *       }
     */
    public Map getAllWorkers(Integer page, Pager<Whereabout> pager){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new ArrayList<>());
    }
}

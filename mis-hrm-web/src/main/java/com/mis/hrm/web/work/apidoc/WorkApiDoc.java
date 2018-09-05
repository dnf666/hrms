package com.mis.hrm.web.work.apidoc;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.work.model.Whereabout;

import java.util.ArrayList;
import java.util.List;
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
     *   @api {DELETE} work (批量)删除成员信息
     *   @apiDescription 根据num组删除成员信息，返回成功删除的成员个数
     *   @apiGroup WORK-DELETE
     *   @apiParam  {List} nums 学号
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": 3
     *       }
     */
    public Map deleteByNums(List<String> nums){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,0);
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
     *   @api {GET} work/all 获取成员列表
     *   @apiDescription 分页获取全部成员信息
     *   @apiGroup WORK-QUERY
     *   @apiParam  {Integer} page 当前页码
     *   @apiParam  {Integer} size 每页数量
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
    public Map getAllWorkers(Integer page, Integer size, Pager<Whereabout> pager){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new ArrayList<>());
    }

    /**
     *   @api {POST} work/filter 筛选成员信息
     *   @apiDescription 根据表单数据筛选成员信息
     *   @apiGroup WORK-QUERY
     *   @apiParam  {Integer} page 当前页码
     *   @apiParam  {Integer} size 每页数量
     *   @apiParam  {Whereabout} whereabout 表单获取到的成员信息
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
    public Map workFilter(String grade, Integer page, Pager<Whereabout> pager){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,new ArrayList<>());
    }
}

package com.mis.hrm.web.book.controller;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.util.ConstantValue;
import com.mis.hrm.util.ToMap;

import java.util.LinkedList;
import java.util.Map;

public class BookLendController {
    /**
     *   @api {GET} booklend-list-1 通过借书者
     *   @apiDescription 通过借书者查询借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiParam  {String} borrower 借书者
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *     	   "bookName": "暗时间",
     * 	       "bookRecord": "不晓得是撒子啊",
     * 	       "borrower": "优秀的人",
     * 	       "companyId": "信管工作室",
     * 	       "lendTime": "2018-08-08",
     * 	       "returnTime": "2018-08-08"
     *          },{
     * 	        "bookName": "暗时间",
     * 	        "bookRecord": "不晓得是撒子啊",
     * 	        "borrower": "优秀的人",
     * 	        "companyId": "信管工作室",
     * 	        "lendTime": "2018-08-08",
     * 	        "returnTime": "2018-08-08"
     *         }]
     *
     *       }
     */
    public Map getBookLendInfosByBorrower(String borrower){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, new LinkedList<BookLendInfo>());
    }

    /**
     *   @api {GET} booklend-list-2 通过公司id查询借书的信息
     *   @apiDescription 通过公司id查询借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *     	   "bookName": "暗时间",
     * 	       "bookRecord": "不晓得是撒子啊",
     * 	       "borrower": "优秀的人",
     * 	       "companyId": "信管工作室",
     * 	       "lendTime": "2018-08-08",
     * 	       "returnTime": "2018-08-08"
     *          },{
     * 	        "bookName": "暗时间",
     * 	        "bookRecord": "不晓得是撒子啊",
     * 	        "borrower": "优秀的人",
     * 	        "companyId": "信管工作室",
     * 	        "lendTime": "2018-08-08",
     * 	        "returnTime": "2018-08-08"
     *         }]
     *
     *       }
     */
    public Map getBookLendInfosByCompanyId(String companyId){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, new LinkedList<BookLendInfo>());
    }

    /**
     *   @api {GET} booklend-list-3 通过公司和书名
     *   @apiDescription 通过公司和书名查询借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} borrower 借书者
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *     	   "bookName": "暗时间",
     * 	       "bookRecord": "不晓得是撒子啊",
     * 	       "borrower": "优秀的人",
     * 	       "companyId": "信管工作室",
     * 	       "lendTime": "2018-08-08",
     * 	       "returnTime": "2018-08-08"
     *          },{
     * 	        "bookName": "暗时间",
     * 	        "bookRecord": "不晓得是撒子啊",
     * 	        "borrower": "优秀的人",
     * 	        "companyId": "信管工作室",
     * 	        "lendTime": "2018-08-08",
     * 	        "returnTime": "2018-08-08"
     *         }]
     *
     *       }
     */
    public Map getBookLendInfosByCompanyIdAndBookName(String companyId, String bookName){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, new LinkedList<BookLendInfo>());
    }

    /**
     *   @api {GET} booklend-list-4 通过公司名和借书的记录
     *   @apiDescription 通过公司名和书的记录查询借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} bookRecord 借书记录
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":{
     *     	   "bookName": "暗时间",
     * 	       "bookRecord": "不晓得是撒子啊",
     * 	       "borrower": "优秀的人",
     * 	       "companyId": "信管工作室",
     * 	       "lendTime": "2018-08-08",
     * 	       "returnTime": "2018-08-08"
     *          }
     *       }
     */
    public Map getBookLendInfosByCompanyIdAndBookRecord(BookLendInfo bookLendInfo){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, BookLendInfo.builder().build());
    }

    /**
     *   @api {GET} booklend-list-５ 得到所有借书信息
     *   @apiDescription 得到所有的借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":[{
     *     	   "bookName": "暗时间",
     * 	       "bookRecord": "不晓得是撒子啊",
     * 	       "borrower": "优秀的人",
     * 	       "companyId": "信管工作室",
     * 	       "lendTime": "2018-08-08",
     * 	       "returnTime": "2018-08-08"
     *          },{
     * 	        "bookName": "暗时间",
     * 	        "bookRecord": "不晓得是撒子啊",
     * 	        "borrower": "优秀的人",
     * 	        "companyId": "信管工作室",
     * 	        "lendTime": "2018-08-08",
     * 	        "returnTime": "2018-08-08"
     *         }]
     *
     *       }
     */
    public Map getBookLendInfos(){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, new LinkedList<BookLendInfo>());
    }


    /**
     *   @api {POST} bookLendInfo 插入借一本书的信息
     *   @apiDescription 插入一本借书的信息
     *   @apiGroup BOOKLEND-ADD
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} bookName 书名
     *   @apiParam  {String} lendTime 借书时间
     *   @apiParam  {String} returnTime 归还时间（可以为空）
     *   @apiParam  {String} borrower 借书者
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map insertBookLendInfo(BookLendInfo bookLendInfo){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, null);
    }


    /**
     *   @api {DELETE} bookLendInfo 通过companyId & bookRecord 删除借书信息
     *   @apiDescription 通过companyId & bookRecord 删除借书信息
     *   @apiGroup BOOKLEND-DELETE
     *   @apiParam  {String} companyId 公司的id
     *   @apiParam  {String} bookRecord 借书记录
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map deleteBookLendInfo(BookLendInfo bookLendInfo){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, null);
    }

    /**
     *   @api {PUT} bookLendInfo 通过companyId & bookRecord 更改借书信息
     *   @apiDescription 通过companyId & bookRecord 更改借书信息，同时返回更新后的信息
     *   @apiGroup BOOKLEND-UPDATE
     *   @apiParam  {String} companyId 公司id
     *   @apiParam {String} bookRecord 借书记录
     *   @apiParam  {String} bookName 书名
     *   @apiParam  {String} lendTime 借书时间
     *   @apiParam  {String} returnTime 归还时间
     *   @apiParam  {String} borrower 借书者
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":{
     *          	   "bookName": "暗时间",
     *       	       "bookRecord": "不晓得是撒子啊",
     *       	       "borrower": "优秀的人",
     *       	       "companyId": "信管工作室",
     *       	       "lendTime": "2018-08-08",
     *       	       "returnTime": "2018-08-08"
     *               }
     *       }
     */
    public Map updateBookLendInfo(BookLendInfo bookLendInfo){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE, ConstantValue.SUCCESS, null);
    }
}

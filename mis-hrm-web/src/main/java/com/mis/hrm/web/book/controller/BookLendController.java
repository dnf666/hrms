package com.mis.hrm.web.book.controller;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.web.util.ControllerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
public class BookLendController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BookLendService bookLendService;
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
    @GetMapping("booklend-list-1")
    public Map getBookLendInfosByBorrower(String borrower){
        Map<String, Object> result;
        try {
            result = ToMap.toSuccessMap(bookLendService.selectBookLendInfosByBorrower(borrower));
        } catch (NullPointerException n) {
            result = ToMap.toFalseMap(n.getMessage());
        }  catch (InfoNotFullyException inf){
            result = ToMap.toFalseMap(inf.getMessage());
        } catch (Exception e){
            logger.error(e.toString());
            result = ToMap.toFalseMapByServerError();
        }
        return result;
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
    @GetMapping("booklend-list-2")
    public Map getBookLendInfosByCompanyId(String companyId){
        Map<String, Object> result;
        try {
            result = ToMap.toSuccessMap(bookLendService.selectBookLendInfosByCompanyId(companyId));
        } catch (NullPointerException n) {
            result = ToMap.toFalseMap(n.getMessage());
        }  catch (InfoNotFullyException inf){
            result = ToMap.toFalseMap(inf.getMessage());
        } catch (Exception e){
            logger.error(e.toString());
            result = ToMap.toFalseMapByServerError();
        }
        return result;
    }

    /**
     *   @api {GET} booklend-list-3 通过公司和书名
     *   @apiDescription 通过公司和书名查询借书信息
     *   @apiGroup BOOKLEND-QUERY
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} bookName 书名
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
    @GetMapping("booklend-list-3")
    public Map getBookLendInfosByCompanyIdAndBookName(String companyId, String bookName){
        Map<String, Object> result;
        try {
            result = ToMap.toSuccessMap(bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId, bookName));
        } catch (NullPointerException n) {
            result = ToMap.toFalseMap(n.getMessage());
        }  catch (InfoNotFullyException inf){
            result = ToMap.toFalseMap(inf.getMessage());
        } catch (Exception e){
            logger.error(e.toString());
            result = ToMap.toFalseMapByServerError();
        }
        return result;
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
    @GetMapping("booklend-list-4")
    public Map getBookLendInfosByCompanyIdAndBookRecord(BookLendInfo bookLendInfo){
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookLendService::selectByPrimaryKey, bookLendInfo);
        return result;
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
    @GetMapping("booklend-list-5")
    public Map getBookLendInfos(){
        Map<String, Object> result;
        try {
            result = ToMap.toSuccessMap(bookLendService.selectAll());
        } catch (NullPointerException n) {
            result = ToMap.toFalseMap(n.getMessage());
        }  catch (InfoNotFullyException inf){
            result = ToMap.toFalseMap(inf.getMessage());
        } catch (Exception e){
            logger.error(e.toString());
            result = ToMap.toFalseMapByServerError();
        }
        return result;
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
    @PostMapping("bookLendInfo")
    public Map insertBookLendInfo(BookLendInfo bookLendInfo){
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookLendService::insert, bookLendInfo);
        return result;
    }


    /**
     *   @api {DELETE} bookLendInfo/{companyId}/{bookRecord} 通过companyId & bookRecord 删除借书信息
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
    @DeleteMapping("bookLendInfo/{companyId}/{bookRecord}")
    public Map deleteBookLendInfo(@PathVariable("companyId")String companyId,
                                  @PathVariable("bookRecord")String bookRecord){
        BookLendInfo bookLendInfo = BookLendInfo.builder().companyId(companyId).bookRecord(bookRecord).build();
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookLendService::deleteByPrimaryKey, bookLendInfo);
        return result;
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
    @PutMapping("bookLendInfo")
    public Map updateBookLendInfo(BookLendInfo bookLendInfo){
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookLendService::updateByPrimaryKey, bookLendInfo);
        return result;
    }
}



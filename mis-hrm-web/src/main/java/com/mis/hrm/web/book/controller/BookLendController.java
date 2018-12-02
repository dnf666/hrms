package com.mis.hrm.web.book.controller;

import com.google.common.base.Strings;
import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.constant.PageConstant;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.web.util.ControllerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("bookLendInfo")
public class BookLendController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BookLendService bookLendService;

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
    @PostMapping("filter")
    public Map searchBook(@RequestBody BookLendInfo bookLendInfo,Integer currentPage,Integer size){
        if (currentPage == null) {
            currentPage = PageConstant.DEFUALT_PAGE;
        }
        if (size == null) {
            size = PageConstant.DEFUALT_SIZE;
        }
        Pager<BookLendInfo> pager = new Pager<>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(size);
        if (Strings.isNullOrEmpty(bookLendInfo.getCompanyId())){
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        List<BookLendInfo> bookList = bookLendService.selectByPrimaryKeyAndPage(bookLendInfo, pager);
        pager.setData(bookList);
        return ToMap.toSuccessMap(pager);
    }
}



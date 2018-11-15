package com.mis.hrm.web.book.controller;


import com.google.common.base.Strings;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;

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
@RequestMapping("book")
public class BookController {
    @Autowired
    private BookService bookService;
    /**
     *   @api {PUT} book 通过书的id更新书的信息
     *   @apiDescription 通过书的id更新书的信息，同时返回更新后的信息
     *   @apiGroup BOOK-UPDATE
     *   @apiParam  {String} bookId 书的id
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} bookName 书名
     *   @apiParam  {String} category 类别
     *   @apiParam  {String} quantity 数量
     *   @apiParam  {String} version 版本
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object":{
     * 	            "bookId": "dddddfffffsssss",
     * 	            "bookName": "你的灯亮着吗？",
     * 	            "category": "思维",
     * 	            "companyId": "信管工作室",
     * 	            "quantity": 2,
     * 	            "version": "2.0"
     *              }
     *       }
     */
    @PutMapping("book")
    public Map updateBookByBookId(Book book){
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookService::updateByPrimaryKey, book);
        return result;
    }

    /**
     *   @api {POST} book 插入一书本的信息
     *   @apiDescription 插入一本书的信息
     *   @apiGroup BOOK-ADD
     *   @apiParam  {String} companyId 公司id
     *   @apiParam  {String} bookName 书名
     *   @apiParam  {String} category 类别
     *   @apiParam  {String} quantity 数量
     *   @apiParam  {String} version 版本
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    @PostMapping("book")
    public Map addBookInfo(@RequestBody Book book){
        Map<String, Object> result;
        if (Strings.isNullOrEmpty(book.getCompanyId())
                || Strings.isNullOrEmpty(book.getBookName())
                || Strings.isNullOrEmpty(book.getVersion())){
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        String companyId = book.getCompanyId();
        String bookName = book.getBookName();
        String version = book.getVersion();
        String bookId = companyId+bookName+version;
        book.setBookId(bookId);
        result = ControllerUtil.getResult(bookService::insert, book);
        return result;
    }

    /**
     *   @api {DELETE} book/{bookId} 通过bookId
     *   @apiDescription 通过bookId删除一本书的信息
     *   @apiGroup BOOK-DELETE
     *   @apiParam  {String} bookId 书的id
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    @PostMapping("delBook")
    public Map deleteBookInfoByBookId(@RequestBody Book book){
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookService::deleteByPrimaryKey, book);
        return result;
    }
    @PostMapping("filter")
    public Map searchBook(@RequestBody Book book,Integer currentPage,Integer size){
        if (currentPage == null) {
            currentPage = PageConstant.DEFUALT_PAGE;
        }
        if (size == null) {
            size = PageConstant.DEFUALT_SIZE;
        }
        Pager<Book> pager = new Pager<>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(size);
        if (Strings.isNullOrEmpty(book.getCompanyId())){
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        List<Book> bookList = bookService.selectByPrimaryKeyAndPage(book, pager);
        pager.setData(bookList);
        return ToMap.toSuccessMap(pager);
    }
}

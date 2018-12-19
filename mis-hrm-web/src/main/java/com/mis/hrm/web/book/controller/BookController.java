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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PutMapping("book")
    public Map updateBookByBookId(@RequestBody Book book) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookService::updateByPrimaryKey, book);
        return result;
    }

    @PostMapping("book")
    public Map addBookInfo(@RequestBody Book book) {
        Map<String, Object> result;
        if (Strings.isNullOrEmpty(book.getCompanyId())
                || Strings.isNullOrEmpty(book.getBookName())
                || Strings.isNullOrEmpty(book.getVersion())) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        result = ControllerUtil.getResult(bookService::insert, book);
        return result;
    }

    @PostMapping("delBook")
    public Map deleteBookInfoByBookId(@RequestBody Book book) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookService::deleteByPrimaryKey, book);
        return result;
    }

    @PostMapping("filter")
    public Map searchBook(@RequestBody Book book, Integer currentPage, Integer size) {
        if (currentPage == null) {
            currentPage = PageConstant.DEFUALT_PAGE;
        }
        if (size == null) {
            size = PageConstant.DEFUALT_SIZE;
        }
        Pager<Book> pager = new Pager<>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(size);
        if (Strings.isNullOrEmpty(book.getCompanyId())) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        List<Book> bookList = bookService.selectByPrimaryKeyAndPage(book, pager);
        pager.setData(bookList);
        return ToMap.toSuccessMap(pager);
    }

    @PostMapping("Excel")
    public Map importBookFromExcel(MultipartFile file, String companyId) {
        try {
            bookService.importBookFromExcel(file, companyId);
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}

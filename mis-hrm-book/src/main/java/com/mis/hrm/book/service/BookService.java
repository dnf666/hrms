package com.mis.hrm.book.service;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.util.BaseService;

import java.util.List;
import java.util.Map;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
public interface BookService extends BaseService<Book> {
    List<Book> selectBookByOptions(Book book);
    List<Book> selectBooksByCompanyId(Book book);

    List<Book> selectBooksByComapnyIdAndCategory(Book book);

    List<Book> selectBooksByCompanyIdAndBookName(Book book);
}

package com.mis.hrm.book.service;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.exception.InfoNotFullyException;

import java.util.List;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
public interface BookService extends BaseService<Book> {

    List<Book> selectBooksByCompanyId(Book book) throws InfoNotFullyException;

    List<Book> selectBooksByComapnyIdAndCategory(Book book) throws InfoNotFullyException;

    List<Book> selectBooksByCompanyIdAndBookName(Book book) throws InfoNotFullyException;
}

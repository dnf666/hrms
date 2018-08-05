package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class BookServiceImpl implements BookService {

    @Resource
    private BookMapper bookMapper;


    @Override
    public int deleteByPrimaryKey(Book key) {
        return 0;
    }

    @Override
    public int insert(Book record) {
        return 0;
    }

    @Override
    public Book selectByPrimaryKey(Book key) {
        return null;
    }

    @Override
    public int updateByPrimaryKey(Book record) {
        return 0;
    }
}

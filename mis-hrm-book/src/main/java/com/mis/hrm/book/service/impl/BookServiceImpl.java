package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.service.BookService;
import com.mis.hrm.util.model.Demo;
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
    public int deleteByPrimaryKey(Demo key) {
        return 0;
    }

    @Override
    public int insert(Demo record) {
       return bookMapper.insert(record);
    }

    @Override
    public Demo selectByPrimaryKey(Demo key) {
        return bookMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(Demo record) {
        return 0;
    }
}

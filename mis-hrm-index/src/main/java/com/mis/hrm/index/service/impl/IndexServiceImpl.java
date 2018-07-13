package com.mis.hrm.index.service.impl;

import com.mis.hrm.index.dao.IndexMapper;
import com.mis.hrm.index.service.IndexService;
import com.mis.hrm.util.model.Demo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private IndexMapper indexMapper;


    @Override
    public int deleteByPrimaryKey(Demo key) {
        return 0;
    }

    @Override
    public int insert(Demo record) {
        return 0;
    }

    @Override
    public Demo selectByPrimaryKey(Demo key) {
        return null;
    }

    @Override
    public int updateByPrimaryKey(Demo record) {
        return 0;
    }
}

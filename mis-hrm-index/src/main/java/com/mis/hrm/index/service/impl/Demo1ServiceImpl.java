package com.mis.hrm.index.service.impl;

import com.mis.hrm.index.dao.Demo1Mapper;
import com.mis.hrm.index.service.Demo1Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class Demo1ServiceImpl implements Demo1Service {

    @Resource
    private Demo1Mapper demo1Mapper;
    @Override
    public int saveDemo() {
        System.out.println("demo1");
        return 0;
    }
}

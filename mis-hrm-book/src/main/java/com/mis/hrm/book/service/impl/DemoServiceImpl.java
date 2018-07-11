package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.DemoMapper;
import com.mis.hrm.book.service.DemoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    private DemoMapper demoMapper;
    @Override
    public int saveDemo() {

        return 0;
    }
}

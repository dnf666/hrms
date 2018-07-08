package com.mis.hrm.service.impl;

import com.mis.hrm.dao.DemoMapper;
import com.mis.hrm.model.Demo;
import com.mis.hrm.service.DemoService;
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

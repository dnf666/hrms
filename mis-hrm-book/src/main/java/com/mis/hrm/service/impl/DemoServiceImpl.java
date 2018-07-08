package com.mis.hrm.service.impl;

import com.mis.hrm.model.Demo;
import com.mis.hrm.service.DemoService;
import org.springframework.stereotype.Service;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public int saveDemo() {
        Demo demo = new Demo();
        return 0;
    }
}

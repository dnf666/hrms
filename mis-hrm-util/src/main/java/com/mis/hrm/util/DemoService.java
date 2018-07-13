package com.mis.hrm.util;

import com.mis.hrm.util.model.Demo;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface DemoService {
    int saveDemo(Demo demo);
    Demo selectByPrimaryKey(String username);
    int deleteDemo(String username);
    int updateDemo(Demo demo);
}

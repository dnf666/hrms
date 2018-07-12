package com.mis.hrm.index.dao;

import com.mis.hrm.index.model.Demo;
import org.springframework.stereotype.Repository;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Repository
public interface Demo1Mapper {
    int saveDemo(Demo demo);
    Demo findDemoByPrimaryKey(String username);
}

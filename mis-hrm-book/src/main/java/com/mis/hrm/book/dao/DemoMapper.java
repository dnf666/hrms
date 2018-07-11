package com.mis.hrm.book.dao;

import org.springframework.stereotype.Repository;
import com.mis.hrm.book.model.Demo;
/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Repository
public interface DemoMapper {
    int saveDemo(Demo demo);
    Demo findDemoByPrimaryKey(String username);
}

package com.mis.hrm.dao;

import com.mis.hrm.model.Demo;
import org.springframework.stereotype.Repository;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Repository
public interface DemoMapper {
    int saveDemo(Demo demo);
}

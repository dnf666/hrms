package com.mis.hrm.login.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * created by dailf on 2018/11/4
 *
 * @author dailf
 */
@Repository
public interface TypeMapper {
    List<String> getMajorType();
    List<String> getViceType(String majorType);
}

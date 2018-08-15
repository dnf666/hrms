package com.mis.hrm.util;

import com.mis.hrm.util.exception.InfoNotFullyExpection;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface BaseService<T> {
    int deleteByPrimaryKey(T key) throws InfoNotFullyExpection;

    int insert(T record) throws InfoNotFullyExpection;

    T selectByPrimaryKey(T key) throws InfoNotFullyExpection;

    int updateByPrimaryKey(T record) throws InfoNotFullyExpection;
}

package com.mis.hrm.util;

import com.mis.hrm.util.exception.InfoNotFullyException;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface BaseService<T> {
    int deleteByPrimaryKey(T key) throws InfoNotFullyException;

    int insert(T record) throws InfoNotFullyException;

    T selectByPrimaryKey(T key) throws InfoNotFullyException;

    int updateByPrimaryKey(T record) throws InfoNotFullyException;
}

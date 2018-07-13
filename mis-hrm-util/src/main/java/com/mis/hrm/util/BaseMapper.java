package com.mis.hrm.util;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface BaseMapper<T> {
    int deleteByPrimaryKey(T key);

    int insert(T record);

    T selectByPrimaryKey(T key);


    int updateByPrimaryKey(T record);
}

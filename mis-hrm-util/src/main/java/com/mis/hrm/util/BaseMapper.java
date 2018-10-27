package com.mis.hrm.util;

import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<T> selectByPrimaryKeyAndPage(@Param("key") T key, @Param("offset") int offset, @Param("size") int size);

    int getCountByKeys(T key);

    int insertMany(List<T> list);
}

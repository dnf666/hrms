package com.mis.hrm.util;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface BaseMapper<T> {
    /**
     * 通过主键删除
     * @param key 条件
     * @return 是否删除成功
     */
    int deleteByPrimaryKey(T key);

    /**
     * 添加记录
     * @param record 记录
     * @return 是否添加成功
     */
    int insert(T record);
    /**
     * 通过主键查询
     * @param key 条件
     * @return 记录
     */
    T selectByPrimaryKey(T key);
    /**
     * 通过主键修改
     * @param record 修改
     * @return 是否修改成功
     */
    int updateByPrimaryKey(T record);

    /**
     * 分页查询
     * @param key 条件
     * @param offset 开始
     * @param size 大小
     * @return
     */
    List<T> selectByPrimaryKeyAndPage(@Param("key") T key, @Param("offset") int offset, @Param("size") int size);

    int getCountByKeys(T key);

    int insertMany(List<T> list);
}

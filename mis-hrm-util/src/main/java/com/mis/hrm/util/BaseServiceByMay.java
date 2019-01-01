package com.mis.hrm.util;

/**
 * @author May
 * 这个接口有点搞事，跟baseservice一样
 */
public interface BaseServiceByMay <T> {
    /**
     * 通过主键删除 要求对其中可能会出现的地方跑异常
     * @param key 键
     */
    void deleteByPrimaryKey(T key);

    /**
     * 添加记录 添加失败抛出异常
     * @param record 一条记录
     * @return 是否添加成功
     */
    void insert(T record);

    /**
     * 通过主键查询
     * @param key 键
     * @return 所有符合要求的记录
     */
    T selectByPrimaryKey(T key);

    /**
     * 通过主键修改 修改失败抛异常
     * @param record 记录
     */
    void updateByPrimaryKey(T record);
}

package com.mis.hrm.util;

/**
 * @author May
 */
public interface BaseServiceByMay <T> {
    /**
     * 通过主键删除 要求对其中可能会出现的地方跑异常
     * @param key
     */
    void deleteByPrimaryKey(T key);

    /**
     * 添加记录 添加失败抛出异常
     * @param record
     * @return 是否添加成功
     */
    void insert(T record);

    /**
     * 通过主键查询
     * @param key
     * @return 所有记录
     */
    T selectByPrimaryKey(T key);

    /**
     * 通过主键修改 修改失败抛异常
     * @param record
     * @return 是否修改成功
     */
    void updateByPrimaryKey(T record);
}

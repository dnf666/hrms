package com.mis.hrm.util;

/**
 * @author May
 */
public interface BaseMapperByMay <T> {
    /**
     * 通过主键删除
     * @param key
     * @return 是否删除成功
     */
    boolean deleteByPrimaryKey(T key);

    /**
     * 添加记录
     * @param record
     * @return 是否添加成功
     */
    boolean insert(T record);

    /**
     * 通过主键查询
     * @param key
     * @return 记录
     */
    T selectByPrimaryKey(T key);

    /**
     * 通过主键修改
     * @param record
     * @return 是否修改成功
     */
    boolean updateByPrimaryKey(T record);
}

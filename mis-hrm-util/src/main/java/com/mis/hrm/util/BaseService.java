package com.mis.hrm.util;

import com.mis.hrm.util.exception.InfoNotFullyException;

import java.util.List;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
public interface BaseService<T> {
    /**
     * 根据条件删除
     * @param key 条件
     * @return 删除数
     * @throws InfoNotFullyException
     */
    int deleteByPrimaryKey(T key) throws InfoNotFullyException;

    /**
     * 添加
     * @param record 记录
     * @return 添加结果
     * @throws InfoNotFullyException
     */
    int insert(T record) throws InfoNotFullyException;

    /**
     * 根据条件查询
     * @param key 条件
     * @return 对象
     * @throws InfoNotFullyException
     */
    T selectByPrimaryKey(T key) throws InfoNotFullyException;

    /**
     * 更新成员
     * @param record 更改记录
     * @return 更新结果
     * @throws InfoNotFullyException
     */
    int updateByPrimaryKey(T record) throws InfoNotFullyException;

    /**
     * 分页查找
     * @param key 条件
     * @param pager 分页
     * @return 结果
     */
    List<T> selectByPrimaryKeyAndPage(T key, Pager<T> pager);

}

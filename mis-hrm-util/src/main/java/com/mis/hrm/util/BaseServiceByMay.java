package com.mis.hrm.util;

import java.util.List;

/**
 * @author May
 */
public interface BaseServiceByMay <T> {
    void deleteByPrimaryKey(T key);

    void insert(T record);

    List<T> selectByPrimaryKey(T key);


    void updateByPrimaryKey(T record);
}

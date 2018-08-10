package com.mis.hrm.util;

import java.util.List;

/**
 * @author May
 */
public interface BaseMapperByMay <T> {
    boolean deleteByPrimaryKey(T key);

    boolean insert(T record);

    List<T> selectByPrimaryKey(T key);


    boolean updateByPrimaryKey(T record);
}

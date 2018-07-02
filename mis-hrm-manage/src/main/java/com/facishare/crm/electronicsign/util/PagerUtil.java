package com.facishare.crm.electronicsign.util;

import com.facishare.crm.electronicsign.predefine.model.vo.Pager;

import java.util.List;

/**
 * created by dailf on 2018/4/24
 * 封装为pager
 * @author dailf
 */
public class PagerUtil<T> {
    public  Pager<T> toPager(int currentPage, int pageSize, long count, List<T> list) {
        Pager<T> pager = new Pager<>();
        pager.setPageSize(pageSize);
        pager.setCurrentPage(currentPage);
        pager.setRecordSize((int)count);
        pager.setData(list);
        return pager;
    }
}

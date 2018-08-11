package com.mis.hrm.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager<T> implements Serializable {
    private static final long serialVersionUID = -6814398952058550296L;
    //每页的数据量
    private int pageSize = 10;
    //初始页码
    private int currentPage = 1;
    //总页数
    private int pageTotal = 0;
    //总数据量
    private int recordSize = 0;
    //？
    private Map<String, Object> params = new HashMap();
    //返回该页的数据
    private List<T> data = new ArrayList();

    public Pager() {
    }

    //该条到第一条的偏移量（根据这个控制当前页面显示的内容）
    public int getOffset() {
        return (this.currentPage - 1) * this.pageSize;
    }

    //每页的数据量
    public int getLimit() {
        return this.getPageSize();
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage < 1 ? 1 : currentPage;
    }

    public int getPageTotal() {
        return this.pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public void setRecordSize(int recordSize) {
        if (recordSize >= 0) {
            this.pageTotal = recordSize / this.pageSize;
            if (recordSize % this.pageSize != 0) {
                ++this.pageTotal;
            }

            this.recordSize = recordSize;
        }

    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> params() {
        this.addParam("limit", this.getLimit());
        this.addParam("offset", this.getOffset());
        this.addParam("pager_case", "limit #{limit} offset #{offset}");
        return this.params;
    }

    @Override
    public String toString() {
        return "Pager{" +
                "pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                ", pageTotal=" + pageTotal +
                ", recordSize=" + recordSize +
                ", params=" + params +
                ", data=" + data +
                '}';
    }
}

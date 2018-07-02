package com.facishare.crm.promotion.predefine.service.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PageArg implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageNumber;
    private Integer pageSize;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        if (pageNumber == null || pageSize == null) {
            return 0;
        }
        return (pageNumber - 1) * pageSize;
    }

    public int getLimit() {
        if (pageNumber == null || pageSize == null) {
            return 1000;
        }
        return pageSize;
    }
}
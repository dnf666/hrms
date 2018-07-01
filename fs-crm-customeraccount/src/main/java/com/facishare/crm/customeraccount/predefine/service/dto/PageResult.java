package com.facishare.crm.customeraccount.predefine.service.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalNumber;
    private Integer totalPage;

    public PageResult(Integer pageNumber, Integer pageSize, Integer totalNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalNumber = totalNumber;
    }
}
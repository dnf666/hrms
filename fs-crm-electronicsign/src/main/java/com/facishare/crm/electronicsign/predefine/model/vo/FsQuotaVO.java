package com.facishare.crm.electronicsign.predefine.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 纷享配额
 */
@Data
public class FsQuotaVO implements Serializable {
    private static final long serialVersionUID = 6040820340602085658L;

    private String id;

    private Long saleIndividualQuota;	//已售出个人配额
    private Long saleEnterpriseQuota;	//已售出企业配额
    private Long saleMoney;     	//已售总金额

    private Long createTime;	        //创建时间
    private Long updateTime;	        //更新时间


}
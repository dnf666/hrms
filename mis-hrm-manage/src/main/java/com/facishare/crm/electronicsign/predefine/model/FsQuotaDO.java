package com.facishare.crm.electronicsign.predefine.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 纷享配额
 */
@Data
@Entity(value = "fsQuota", noClassnameStored = true)
public class FsQuotaDO implements Serializable {
    @Id
    private String id;

    private Long saleIndividualQuota;	//已售出个人配额
    private Long saleEnterpriseQuota;	//已售出企业配额
    private Long saleMoney;     	//已售总金额

    private Long createTime;	        //创建时间
    private Long updateTime;	        //更新时间
}
package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 租户配额
 */
@Data
@Entity(value = "tenantQuota", noClassnameStored = true)
public class TenantQuotaDO implements Serializable {
    @Id
    private String id;
    private String tenantId;	                    //租户id
    /**
     * @see QuotaTypeEnum
      */
    private String quotaType;	                    //配额类型
    private Integer usedQuota;	                    //已用配额
    private Integer buyQuota;	                    //购买配额
    private Long payMoney;                          //购买金额
    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}
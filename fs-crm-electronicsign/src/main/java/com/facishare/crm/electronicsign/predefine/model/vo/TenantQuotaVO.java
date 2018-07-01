package com.facishare.crm.electronicsign.predefine.model.vo;

import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 租户配额
 */
@Data
public class TenantQuotaVO implements Serializable {
    private static final long serialVersionUID = -487408558959450754L;

    private String id;
    private String tenantId;	                    //租户id
    /**
     * @see QuotaTypeEnum
     */
    private String quotaType;	                    //配额类型
    private String enterpriseName;                  //企业名称
    private Integer remainedQuota;                  //剩余配额
    private Integer usedQuota;	                    //已用配额
    private Integer buyQuota;	                    //购买配额
    private Long payMoney;                          //购买金额
    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}
package com.facishare.crm.electronicsign.predefine.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 配额余额告警
 */
@Data
public class RemainedQuotaAlarmVO implements Serializable {
    private static final long serialVersionUID = 745068773295545960L;

    private String id;
    private String tenantId;	                    //租户id

    private Integer individualAlarmNum;	            //个人告警数量
    private Integer enterpriseAlarmNum;	            //企业告警数量
    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}
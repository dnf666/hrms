package com.facishare.crm.electronicsign.predefine.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

/**
 * 配额余额告警
 */
@Data
@Entity(value = "remainedQuotaAlarm", noClassnameStored = true)
public class RemainedQuotaAlarmDO implements Serializable {
    @Id
    private String id;
    private String tenantId;	                    //租户id

    private Integer individualAlarmNum;	            //个人告警数量
    private Integer enterpriseAlarmNum;	            //企业告警数量
    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}

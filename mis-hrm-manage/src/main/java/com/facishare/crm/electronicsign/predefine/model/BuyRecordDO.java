package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

/**
 * 购买记录
 */
@Data
@Entity(value = "buyRecord", noClassnameStored = true)
public class BuyRecordDO implements Serializable {
    @Id
    private String id;
    private String tenantId;	                    //租户id

    private Integer operatorId;		                //操作人id
    /**
     * @see com.facishare.crm.electronicsign.enums.type.BuyRecordOperatorTypeEnum
     */
    private Integer operatorType;                   // 操作人类型
    /**
     * @see QuotaTypeEnum
     */
    private String quotaType;		                //配额类型
    private String payType;		                    //支付方式
    private Long payMoney;		                    //付款金额（单位：分）（支付组也是用分）
    private Integer buyQuota;		                //购买配额
    private Long buyTime;		                    //充值时间
}
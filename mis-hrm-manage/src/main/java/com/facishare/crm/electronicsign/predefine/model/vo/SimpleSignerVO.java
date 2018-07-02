package com.facishare.crm.electronicsign.predefine.model.vo;

import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 签署者
 */
@Data
public class SimpleSignerVO implements Serializable {
    /**
     * @see SignerTypeEnum
     */
    private Integer signerType;                     //签署者类型
    private String accountId;		                //客户id       （签署者是客户时才需要）
    private String upDepartmentId;		            //上游签署部门id（签署者是租户时才需要）
}

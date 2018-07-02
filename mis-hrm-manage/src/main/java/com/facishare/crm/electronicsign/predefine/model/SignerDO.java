package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

/**
 * 签署者
 */
@Data
public class SignerDO implements Serializable {
    @Id
    private String id;
    /**
     * @see SignerTypeEnum
     */
    private Integer signerType;                     //签署者类型
    /**
     * @see SignTypeEnum
     */
    private Integer signType;                       //签署类型
    private String accountId;		                //客户id    （ 签署者是客户时才需要）
    private String upDepartmentId;		            //上游签署部门id（签署者是租户时才需要）
    private Integer orderNum;		                //序号
    private String bestSignAccount;		            //上上签账号	（保存下来，不用每次都查）
    private SignPositionDO signPosition;            //签署位置
}
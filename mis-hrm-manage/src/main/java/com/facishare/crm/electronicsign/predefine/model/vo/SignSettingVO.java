package com.facishare.crm.electronicsign.predefine.model.vo;

import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 签署签署设置
 */
@Data
public class SignSettingVO implements Serializable {
    private static final long serialVersionUID = 6206371686236675070L;

    private String id;
    private String tenantId;	                    //租户id
    /**
     * @see AppTypeEnum
     */
    private String appType;	                        //应用类型
    private String objApiName;	                    //对象apiName

    private List<SignerSettingVO> signerSettings;
    private Boolean isHasOrder;	                    //是否有序

    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}
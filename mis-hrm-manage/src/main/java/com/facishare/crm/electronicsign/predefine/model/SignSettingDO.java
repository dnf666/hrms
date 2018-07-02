package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;
import java.util.List;

/**
 * 签署设置
 */
@Data
@Entity(value = "signSetting", noClassnameStored = true)
public class SignSettingDO implements Serializable {
    @Id
    private String id;
    private String tenantId;	                    //租户id
    /**
     * @see AppTypeEnum
     */
    private String appType;	                        //应用类型
    private String objApiName;	                    //对象apiName

    private List<SignerSettingDO> signerSettings;
    private Boolean isHasOrder;	                    //是否有序

    private Long createTime;	                    //创建时间
    private Long updateTime;	                    //更新时间
}
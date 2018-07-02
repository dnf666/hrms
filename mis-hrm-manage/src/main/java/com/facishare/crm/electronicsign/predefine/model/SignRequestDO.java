package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import java.io.Serializable;
import java.util.List;

/**
 * 签署请求
 */
@Data
@Entity(value = "signRequest", noClassnameStored = true)
public class SignRequestDO implements Serializable {
    @Id
    private String id;
    private String tenantId;	                    //租户id
    private Integer upCreateContractUserId;     	//上游触发创建合同的人员Id
    private Integer upUploadContractUserId;     	//上传合同到文件系统，用这个人

    /**
     * @see AppTypeEnum
     */
    private String appType;     	                //应用类型
    private String objApiName;		                //对象ApiName
    private String objDataId;		                //对象数据Id

    private String contractFileAttachmentName;      //合同附件的文件名，保持合同附件时需要
    private List<SignerDO> signers;
    private String templateBestSignFileId;          //打印模板文件上传到上上签后，拿到的fileId  （合同会过期，过期了需要用templateBestSignFileId重新创建合同）

    private String contractId;		                //合同id
    private Long contactExpireTime;		            //合同到期时间

    private Long createTime;		                //创建时间
    private Long updateTime;		                //创建时间
}
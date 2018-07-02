package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.*;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.type.CertifyTypeEnum;
import com.facishare.crm.electronicsign.enums.type.LegalPersonIdentityTypeEnum;
import com.facishare.crm.electronicsign.enums.type.UserIdentityTypeEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

/**
 * 认证记录
 */
@Data
@Entity(value = "certifyRecord", noClassnameStored = true)
public class CertifyRecordDO implements Serializable {
    @Id
    private String id;
    private String tenantId;                       //租户id

    /**
     * @see UserObjEnum
     */
    private Integer userObj;		                //用户对象

    /**
     * @see CertifyTypeEnum
     */
    private Integer certifyType;		            //认证类型
    private String accountId;		                //客户id	客户认证时要存
    private String bestSignAccount;                 //实名账户
    /**
     * 注册时返回的，异步申请证书队列中的任务编号，在24小时内可用于查询异步申请状态，taskId过24小时后就失效
     * 回调的时候用来定位是哪条申请记录
     */
    private String taskId;


    private String enterpriseName;	                //企业名称
    private String unifiedSocialCreditIdentifier;	//统一社会信用代码
    private String legalPersonName;	                //法人或经办人姓名
    private String legalPersonIdentity;         	//法人或经办人证件号
    /**
     * @see LegalPersonIdentityTypeEnum
     */
    private String legalPersonIdentityType;	        //法人或经办人证件类型
    private String legalPersonMobile;               //法人或经办人手机号


    private String userName;	                   //姓名
    private String userIdentity;	               //证件号
    /**
     * @see UserIdentityTypeEnum
     */
    private String userIdentityType;               //证件类型
    private String userMobile;	                   //手机号


    /**
     * @see CertifyStatusEnum
     */
    private String certifyStatus;	                //状态
    private String certifyErrMsg;

    private Long createTime;                    	//创建时间
    private Long updateTime;                    	//更新时间
}
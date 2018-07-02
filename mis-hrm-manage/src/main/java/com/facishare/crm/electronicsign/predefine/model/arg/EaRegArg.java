package com.facishare.crm.electronicsign.predefine.model.arg;

import com.facishare.crm.electronicsign.enums.type.UserTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 上上签企业注册参数
 */
@Data
public class EaRegArg implements Serializable {
    private static final long serialVersionUID = 4117064272958536763L;

    private String account;	       //用户帐号
    private String name;	       //企业名称
    /**
     * @see UserTypeEnum
     */
    private String userType;	   //用户类型
    private String mail;	       //用户邮箱
    private String mobile;	       //用户手机号
    private Credential credential; //企业证件信息对象
    private String applyCert;      //是否申请证书, 申请填写为1

    @Data
    public static class Credential {
        private String regCode;	 	   	        //工商注册号
        private String orgCode;	 	   	   	    //组织机构代码
        private String taxCode;	   	   	   	    //税务登记证号
        private String legalPerson;	            //法人代表姓名
        private String legalPersonIdentity;	    //法人代表证件号
        private String legalPersonIdentityType;	//法人代表证件类型
        private String legalPersonMobile;	    //法人代表手机号
        private String contactMobile;	        //联系手机
        private String contactMail;	            //联系邮箱
        private String province;	            //省份
        private String city;	                //城市
        private String address;	                //地址
    }
}

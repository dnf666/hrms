package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.enums.status.TotalSignStatusEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import com.facishare.crm.electronicsign.predefine.model.vo.SignerHasSignStatusVO;
import lombok.Data;

import java.util.List;

@Data
public class SignRequestType {
    /**
     * 是否有签署权限
     */
    public static class IsHasSignPermission {
        @Data
        public static class Result {
            //1：可以 其他：不可以
            private int status = 1;
            private String message = "success";
        }

        @Data
        public static class Arg {
            private String appType;                        //【必填】
            private String accountId;                      //【必填】
        }
    }

    /**
     * 获取签署URL
     */
    public static class GetSignUrl {
        @Data
        public static class Result {
            //1：成功 2：失败
            private int status = 1;
            private String message = "success";
            private Integer signType;
            private String signUrl;
            private Boolean hasRecreateContract;
            private List<SignerArg> hasSignOldContractSigners;
        }

        @Data
        public static class Arg {
            /**
             * @see AppTypeEnum
             */
            private String appType;                        //【必填】
            private String objApiName;                     //【必填】
            private String objDataId;                      //【必填】

            private Boolean isReCreateContractIfExpired;   //【必填】如果合同到期，是否重新创建合同
            private String urlExpireTime;                  //【选填】由于返回的是短链接，如果没有设置则默认是30分钟，如果设置了以设置的为准，但有效期最大不能超过7天，如希望“2017/12/30 10:21:52”到期，则设置为“1514600512”（秒级的unix时间戳）
            private String isAllowChangeSignaturePosition; //【选填】手动签是否允许拖动签名位置（"0"：不允许，"1"：允许）（默认"1"）
            private String vcodeMobile;                    //【选填】如果设置了，手动签时，签署者需要用这个手机号的验证码才能完成签署
            private String signedReturnUrl;
            private SignerArg signer;
            private Integer upCreateContractUserId;        //【第一次必填】创建合同，会找这个人的主属部门，如果主属部门没有上上签账号，就一直往上级父部门找
            private Integer upUploadContractUserId;        //【第一次必填】后面上传合同到文件系统以这个人为上传者

            private String contractFileAttachmentName;
            private String title;
            private Long contractExpireTime;
        }

        @Data
        public static class SignerArg {
            /**
             * @see SignerTypeEnum
             */
            private Integer signerType;
            private String accountId; // 客户签署需要传
            private String upDepartmentId;
            private Integer orderNum;
        }
    }

    /**
     * 签署结果回调
     */
    public static class SignResultCallBack {
        @Data
        public static class Result {
            //1：成功 其他：失败
            private int status = 1;
            private String message = "success";
        }

        @Data
        public static class Arg {
            private String contractId;
            private String account;
            private String signerStatus;  //签署状态： 2：已完成； 非2就是失败了。。业务不能往下走
            private String errMsg;        //签署成功为success，签署失败为相应的错误信息
        }
    }

    /**
     * 查询签署状态
     */
    public static class GetSignStatus {
        @Data
        public static class Result {
            //1：成功 其他：失败
            private int status = 1;
            private String message = "success";
            /**
             * @see TotalSignStatusEnum
             */
            private String totalSignStatus;
            private List<SignerHasSignStatusVO> signerStatus;
        }

        @Data
        public static class Arg {
            private String appType;                        //【必填】
            private String objApiName;                     //【必填】
            private String objDataId;                      //【必填】
        }
    }
}
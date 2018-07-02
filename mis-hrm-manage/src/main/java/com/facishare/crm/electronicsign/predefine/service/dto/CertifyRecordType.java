package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.enums.status.BestSignCertifyStatusEnum;
import com.facishare.crm.electronicsign.predefine.model.vo.CertifyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * created by dailf on 2018/4/26
 *
 * @author dailf
 */
@Data
public class CertifyRecordType {
    @Data
    public static class GetCertifyRecordByPage {
        @Data
        public static class Result {
            private Pager<CertifyRecordVO> pager;
        }

        @Data
        public static class Arg {
            private Integer pageSize;
            private Integer currentPage;
            private Integer userObj;
            private Integer certifyType;
            private String certifyStatus;
            private Long startTime;
            private Long endTime;
        }
    }

    /**
     * 认证回调
     */
    public static class CertCallBack {
        @Data
        public static class Result {
            //1：成功 其他：失败
            private int status = 1;
            private String message = "success";
        }

        @Data
        public static class Arg {
            private String certType;
            private String cert;
            private String message;
            private String account;
            private String taskId;
            /**
             * @see BestSignCertifyStatusEnum
             */
            private String status;
        }
    }
}

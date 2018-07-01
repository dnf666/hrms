package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import lombok.Data;

@Data
public class InternalSignCertifyType {
    @Data
    public static class EnableOrDisable {
        @Data
        public static class Result {
            private Integer status = 1;
            private String message = "success";
        }

        @Data
        public static class Arg {
            private String internalSignCertifyId;
            /**
             * @see UseStatusEnum
             */
            private String useStatus;
        }
    }
}

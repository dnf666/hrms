package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.enums.status.ElecSignInitStatusEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SwitchTypeEnum;
import com.facishare.crm.electronicsign.predefine.model.vo.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ElecSignType {
    /**
     * 查询"发货单开关"是否开启
     */
    @Data
    public static class GetTenantElecSignInfoResult {
        /**
         * 是否灰度   1 是  2 否
         */
        private Integer isGrayed;
        /**
         * @see ElecSignInitStatusEnum
         */
        private int initStatus;
        private int tenantElecSignSwitch;
        private int remainderAlarmSwitch;
        private List<TenantQuotaVO> tenantQuotas;
        private List<BuyRecordVO> buyRecords;
    }

    /**
     *  初始化
     */
    public static class InitElecSign {
        @Data
        public static class Result {
            /**
             * @see ElecSignInitStatusEnum
             */
            private Integer initStatus = 2;
            private String message = "success";
        }
    }

    public static class EnableSwitchForApp {
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Result {
            private int status = 1;
            private String message = "ok";
        }

        @Data
        public static class Arg {
            /**
             * @see com.facishare.crm.electronicsign.enums.type.AppTypeEnum
             */
            private String appType;
            /**
             * @see com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum
             */
            private Integer status;
        }
    }

    /**
     * 开启/关闭开关
     */
    public static class EnableOrDisableTenantSwitch {
        @Data
        public static class Result {
            //1：成功 2：失败
            private int status = 1;
            private String message = "success";
        }

        @Data
        public static class Arg {
            /**
             * @see SwitchTypeEnum
             */
            private int switchType;
            private int status;      // 1：开启 2：关闭
        }
    }

    public static class QueryAppSwitchAndSignSetting {
        @Data
        public static class Result {
            private Integer platformSwitch;
            private Integer appSwitch;
            private List<SignSettingVO> signSettings;
        }

        @Data
        public static class Arg {
            /**
             * @see com.facishare.crm.electronicsign.enums.type.AppTypeEnum
             */
            private String appType;
        }
    }

    public static class DeleteSignSetting {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            //1：成功  其他：失败
            private Integer status = 1;
            private String message = "ok";
        }

        @Data
        public static class Arg {
            private String signSettingId;
            /**
             * @see AppTypeEnum
             */
            private String appType;
        }
    }

    public static class SaveOrUpdateSignSetting {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            //1：成功  其他：失败
            private Integer status = 1;
            private String message = "ok";
        }

        @Data
        public static class Arg {
            /**
             * @see AppTypeEnum
             */
            private String appType;
            private String objApiName;
            private List<SignerSettingVO> signerSettings;
            private Boolean isHasOrder;
        }
    }
}
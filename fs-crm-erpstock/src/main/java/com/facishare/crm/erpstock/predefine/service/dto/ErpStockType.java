package com.facishare.crm.erpstock.predefine.service.dto;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/8
 */
@Data
public class ErpStockType {

    public enum ErpStockSwitchEnum {
        UNABLE(0, "未开启"), FAILED(1, "开启失败"), ENABLE(2, "已经开启");
        private int status;
        private String label;

        ErpStockSwitchEnum(int status, String label) {
            this.status = status;
            this.label = label;
        }
        public static ErpStockSwitchEnum valueOf(int status) {
            for (ErpStockSwitchEnum switchStatus : values()) {
                if (switchStatus.getStatus() == status) {
                    return switchStatus;
                }
            }
            return null;
        }

        public String getLabel() {
            return label;
        }

        public int getStatus() {
            return status;
        }

        public String getStringStatus() {
            return String.valueOf(this.status);
        }
    }

    @Data
    public static class EnableErpStockResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
    }

    @Data
    public static class CloseErpStockResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
    }
}

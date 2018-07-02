package com.facishare.crm.outbounddeliverynote.predefine.service.dto;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/20
 */
public class OutboundDeliveryNoteType {
    public enum OutboundDeliveryNoteSwitchEnum {
        UNABLE(0, "未开启"), FAILED(1, "开启失败"), ENABLE(2, "已经开启");
        private int status;
        private String label;

        OutboundDeliveryNoteSwitchEnum(int status, String label) {
            this.status = status;
            this.label = label;
        }
        public static OutboundDeliveryNoteSwitchEnum valueOf(int status) {
            for (OutboundDeliveryNoteSwitchEnum switchStatus : values()) {
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
    public static class EnableOutboundDeliveryNoteResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
    }
}

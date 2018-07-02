package com.facishare.crm.stock.predefine.service.dto;

import lombok.Data;

public class OrderCheckType {

    public enum OrderCheckTypeEnum {
        CANNOTSUBMIT(1, "库存不足不允许提交订单"), CANSUBMIT(2, "库存不足允许提交订单");
        private int status;
        private String message;

        OrderCheckTypeEnum(int status, String message) {
            this.status = status;
            this.message = message;
        }
        public static OrderCheckTypeEnum valueOf(int status) {
            for (OrderCheckTypeEnum orderCheckTypeEnum : values()) {
                if (orderCheckTypeEnum.getStatus() == status) {
                    return orderCheckTypeEnum;
                }
            }
            return null;
        }

        public String getMessage() {
            return this.message;
        }

        public int getStatus() {
            return this.status;
        }

        public String getStringStatus() {
            return String.valueOf(this.getStatus());
        }
    }

    @Data
    public static class OrderCheckResult {
        /**
         * 0 库存不足允许提交订单
         * 1 库存不足不允许提交订单
         */
        private int Status;
        private String message;
    }
}

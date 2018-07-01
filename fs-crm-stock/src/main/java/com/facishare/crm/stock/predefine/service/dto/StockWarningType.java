package com.facishare.crm.stock.predefine.service.dto;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/26
 */
@Data
public class StockWarningType {
    public enum StockWarningTypeEnum {
        UNABLE(1, "不开启"), ENABLE(2, "开启");

        private int status;
        private String message;

        StockWarningTypeEnum(int status, String message) {
            this.status = status;
            this.message = message;
        }
        public static StockWarningTypeEnum valueOf(int status) {
            for (StockWarningTypeEnum type : values()) {
                if (type.getStatus() == status) {
                    return type;
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

}

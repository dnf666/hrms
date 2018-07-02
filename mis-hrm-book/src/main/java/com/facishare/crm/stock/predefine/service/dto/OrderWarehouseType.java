package com.facishare.crm.stock.predefine.service.dto;

import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/2
 */
@Data
public class OrderWarehouseType {
    public enum OrderWarehouseTypeEnum {
        SINGLE_WAREHOUSE(1, "指定仓库订货"), ALL_WAREHOUSE(2, "合并仓库订货");

        private int status;
        private String message;

        OrderWarehouseTypeEnum(int status, String message) {
            this.status = status;
            this.message = message;
        }
        public static OrderWarehouseTypeEnum valueOf(int status) {
            for (OrderWarehouseTypeEnum orderType : values()) {
                if (orderType.getStatus() == status) {
                    return orderType;
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

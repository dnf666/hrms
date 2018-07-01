package com.facishare.crm.stock.predefine.service.dto;

import lombok.Data;

@Data
public class StockViewType {

    public enum StockViewTypeEnum {
        NO(1, "不显示库存"), ACCURATE(2, "精确显示库存"), FUZZY(3, "模糊显示库存");

        private int status;
        private String message;

        StockViewTypeEnum(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public int getStatus() {
            return this.status;
        }

        public String getStringStatus() {
            return String.valueOf(this.status);
        }


        public static StockViewTypeEnum valueOf(int status) {
            for (StockViewTypeEnum stockViewTypeEnum : values()) {
                if (stockViewTypeEnum.getStatus() == status) {
                    return stockViewTypeEnum;
                }
            }
            return null;
        }
    }
}

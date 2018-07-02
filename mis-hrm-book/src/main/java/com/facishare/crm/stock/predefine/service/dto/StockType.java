package com.facishare.crm.stock.predefine.service.dto;

import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/9.
 */
@Data
public class StockType {

    public enum StockSwitchEnum {
        UNABLE(0, "未开启"), FAILED(1, "开启失败"), ENABLE(2, "已经开启");
        private int status;
        private String label;

        StockSwitchEnum(int status, String label) {
            this.status = status;
            this.label = label;
        }
        public static StockSwitchEnum valueOf(int status) {
            for (StockSwitchEnum switchStatus : values()) {
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
    public static class EnableStockResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
    }

    @Data
    public static class EnableRequisitionResult {
        /**
         * 0 未开启
         * 1 开启失败
         * 2 已经开启
         */
        private int enableStatus;
        private String message;
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

    @Data
    public static class QueryProductByIdsResult {
        private List<Map<String, Object>> value;
        private Map<String, Object> objectDescribe;
    }

    @Data
    public static class QueryProductByIdsArg {
        private List<String> productIds;
    }

    @Data
    public class StockVO {
        @SerializedName("_id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("real_stock")
        private BigDecimal realStock;
        @SerializedName("available_stock")
        private BigDecimal availableStock;
        @SerializedName("blocked_stock")
        private BigDecimal blockedStock;
        @SerializedName("safety_stock")
        private BigDecimal safetyStock;
        @SerializedName("product_id")
        private String productId;
        @SerializedName("warehouse_id")
        private String wareHouseId;
    }
}

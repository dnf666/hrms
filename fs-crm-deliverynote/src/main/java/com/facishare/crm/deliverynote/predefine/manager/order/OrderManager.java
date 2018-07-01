package com.facishare.crm.deliverynote.predefine.manager.order;

import com.facishare.crm.deliverynote.util.ConfigCenter;
import com.facishare.crm.deliverynote.util.HttpUtil;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OrderManager {

    /**
     * 订单通知
     */
    public OrderNotifyResult orderNotify(OrderNotifyArg arg) {
        String orderNotifyUrl = ConfigCenter.ORDER_NOTIFY_URL;
        try {
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");
            OrderNotifyResult result = HttpUtil.post(orderNotifyUrl, headers, arg, OrderNotifyResult.class);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class OrderNotifyResult {
        private String traceId;
        @SerializedName("Error")
        private Error error;
        @SerializedName("Value")
        private boolean value;
    }

    @Data
    @SuppressWarnings("all")
    public static class Error {
        private String Code;
        private String Message;
    }

    @Data
    @Builder
    public static class OrderNotifyArg {
        private String tenantId;
        private String orderId;
        /**
         * 消息类型：3-代表订单发货类型
         */
        private String type;
        /**
         * 发货单编号
         */
        private String typeobjectid;
    }
}

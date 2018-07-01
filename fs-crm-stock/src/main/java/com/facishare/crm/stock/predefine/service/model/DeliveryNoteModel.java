package com.facishare.crm.stock.predefine.service.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liangk
 * @date 15/01/2018
 */
public class DeliveryNoteModel {

    @Data
    public static class DeliveryNoteProductVO {
        private String productId;
        private BigDecimal deliveryNum;
    }

    @Data
    public static class Arg {
        private String deliveryGoodsWarehouseId;
        private String salesOrderId;

        private List<DeliveryNoteProductVO> deliveryNoteProductVOS;
    }
}

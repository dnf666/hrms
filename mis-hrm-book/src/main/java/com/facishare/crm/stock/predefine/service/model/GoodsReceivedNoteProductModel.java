package com.facishare.crm.stock.predefine.service.model;

import com.facishare.paas.metadata.api.IObjectData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/13.
 */
public class GoodsReceivedNoteProductModel {
    @Data
    public static class GoodsReceivedNoteProductVO {

        private String productId;

        private BigDecimal num;

        private String warehouseId;
    }

    @Data
    public static class BuildProductResult {
        private List<String> productIds;
        private Map<String, BigDecimal> productId2NumMap;
    }
}

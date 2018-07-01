package com.facishare.crm.rest.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by linchf on 2018/1/30.
 */
public class CheckBulkAddOrderModel {
    @Data
    public static class Arg {
        String id;//标识id
        String customerId;
        String warehouseName;

        String tradeId;
        String tradeProductId;

        String dataId; //退货单
        String productId;
    }

    @Data
    public static class Result {
        List<DetailResult> successResult;
        List<DetailResult> failedResult;
    }

    @Data
    public static class DetailResult {

        private Boolean isSalesOrderFail;//是否订单校验不通过
        private String tradeId;
        private String tradeProductId;

        private Boolean isReturnedGoodsInvoiceFail;//是否退货单校验不通过
        private String dataId;//退货单id
        private String productId;//产品id

        private String id;//识别id
        private String warehouseId;//仓库id
        private String errCode;
        private String errMessage;
    }

}

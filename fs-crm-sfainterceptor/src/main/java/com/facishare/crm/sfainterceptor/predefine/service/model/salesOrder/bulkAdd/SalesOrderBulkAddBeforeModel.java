package com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chenzengyong
 * @date on 2018/1/13.
 */
@Data
@ToString
public class SalesOrderBulkAddBeforeModel {

    @Data
    @ToString
    public static class Arg {
        private Boolean isCheckSalesOrder;//是否校验订单
        private Boolean isCheckSalesOrderProduct;//是否校验订单产品
        private List<MixtureVo> mixtureVos;
    }


    @Data
    @ToString
    public static class MixtureVo {
        private String id;//标识id

        private String tradeId;//订单id
        private String customerId;//客户id
        private String warehouseName;//仓库名称

        private String tradeProductId;//订单产品id
        private String productId;//产品id
        private String productName;//产品名称
        private BigDecimal amount;//产品数量

    }


    @Data
    @ToString
    public static class Result {
        List<MixtureResult> successResults;//成功校验
        List<MixtureResult> failResults;

        //无意义，满足.net的需求
        private String info = "info";
    }

    @Data
    @ToString
    public static class MixtureResult {
        private String id;//识别id
        private Boolean isSalesOrderFail;//是否订单校验不通过
        private String tradeId;
        private String tradeProductId;
        private String warehouseId;//仓库id
        private String errCode;
        private String errMessage;
    }


}

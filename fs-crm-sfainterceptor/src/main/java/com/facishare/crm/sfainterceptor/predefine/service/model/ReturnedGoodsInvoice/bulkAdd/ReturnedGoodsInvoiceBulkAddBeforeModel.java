package com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd;

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
public class ReturnedGoodsInvoiceBulkAddBeforeModel {

    @Data
    @ToString
    public static class Arg {
        private List<MixtureVo> mixtureVos;//混合对象（退货单+退货单产品）
        private Boolean isCheckReturnedGoodsInvoice;//是否校验退货单
        private Boolean isCheckProduct;//是否校验退货单产品
    }


    @Data
    @ToString
    public static class MixtureVo {
        private String id;//标识id

        private String tradeId;//订单id
        private String dataId;//退货单id
        private String customerId;//客户id
        private String warehouseName;//仓库名称


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
        private Boolean isReturnedGoodsInvoiceFail;//是否退货单校验不通过
        private String dataId;//退货单id
        private String productId;//产品id
        private String warehouseId;//仓库id
        private String errCode;
        private String errMessage;
    }


}

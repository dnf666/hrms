package com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.bulkAdd;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chenzengyong
 * @date on 2018/1/22.
 */
@Data
@ToString
public class ReturnedGoodsInvoiceBulkAddTransactionAfterModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        private List<MixtureVo> mixtureVos;//混合对象（退货单+退货单产品）
    }


    @Data
    @ToString
    public static class MixtureVo {
        private String id;//标识id
        private Boolean isCheckReturnedGoodsInvoice;//是否校验退货单
        private String tradeId;//订单id
        private String dataId;//退货单id
        private String customerId;//客户id
        private String warehouseId;//仓库id

        private Boolean isCheckProduct;//是否校验订单产品
        private String productId;//产品id
        private String productName;//产品名称
        private BigDecimal amount;//产品数量

    }



}

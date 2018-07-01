package com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.bulkAdd;

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
public class SalesOrderBulkAddTransactionAfterModel extends CommonModel{

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
        private String warehouseId;//仓库id

        private String tradeProductId;//订单产品id
        private String productId;//产品id
        private String productName;//产品名称
        private BigDecimal amount;//产品数量

    }

}

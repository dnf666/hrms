package com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit;

import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.sfainterceptor.predefine.service.model.edit.EditBeforeModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class SalesOrderEditBeforeModel extends EditBeforeModel {

    @Data
    @ToString
    public static class Arg {
        private SalesOrderVo salesOrderVo;
        String nowLifeStatus;
    }

    @Data
    @ToString
    public static class SalesOrderVo {
        private String tradeId;//订单id
        private String customerId;//客户id
        private String warehouseId;//仓库id

        private List<SalesOrderProductVo> salesOrderProductVos;
    }

    @Data
    @ToString
    public static class Result {
        private String info = "info";
        private String warehouseId;
    }
}

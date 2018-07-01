package com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.add;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class SalesOrderAddBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        private SalesOrderVo salesOrderVo;
    }

    @Data
    @ToString
    public static class SalesOrderVo {
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

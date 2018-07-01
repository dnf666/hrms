package com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit;

import com.facishare.crm.sfainterceptor.predefine.service.model.common.AfterCommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class SalesOrderEditAfterModel extends AfterCommonModel {
    @Data
    public static class Arg {
        private SalesOrderVo salesOrderVo;
        String dataId;
        String beforeLifeStatus;
        String afterLifeStatus;
    }

    @Data
    @ToString
    public static class SalesOrderVo {
        private String tradeId;//订单id
        private String customerId;//客户id
        private String warehouseId;//仓库id

        private List<SalesOrderProductVo> salesOrderProductVos;
    }
}

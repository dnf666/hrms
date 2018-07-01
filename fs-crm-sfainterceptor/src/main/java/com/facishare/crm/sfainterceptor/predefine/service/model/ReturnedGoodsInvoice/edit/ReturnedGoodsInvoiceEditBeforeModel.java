package com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.edit;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.edit.EditBeforeModel;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class ReturnedGoodsInvoiceEditBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        private ReturnedGoodsInvoiceVo returnedGoodsInvoiceVo;
        String nowLifeStatus;
    }

    @Data
    @ToString
    public static class ReturnedGoodsInvoiceVo {
        private String dataId;
        private String customerId;
        private String warehouseId;
        private String tradeId;
    }

}
